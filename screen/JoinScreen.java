package com.mechwreck.screen;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mechwreck.Background;
import com.mechwreck.SkinManager;
import com.mechwreck.wireless.NameRequest;
import com.mechwreck.wireless.NameResponse;
import com.mechwreck.wireless.WifiHelper;

/**
 * Screen where the player looks for and joins host servers.
 */
public class JoinScreen implements Screen {
	
	private Game game;
	private String name;

	private Client client;
	private Listener listener;
	private List<InetAddress> addresses;
	private ArrayList<String> serverNames;
	private int currentRequestId;
	
	private OrthographicCamera camera;
	private Background background;

	private Stage ui;
	private Table serverTable;
	private TextField nameField;
	
	private Texture texture;
	private Texture texture2;
	private SpriteBatch spriteBatch;
	private boolean showG;
	private boolean showA;
	
	/**
	 * Creates a new join screen.
	 */
	public JoinScreen(Game game) {
		this.game = game;
		name = "Unknown";
		serverNames = new ArrayList<String>();
		currentRequestId = 0;
		
		camera = new OrthographicCamera();
		background = new Background();
		
		texture = new Texture("textures/temp_file.png");
		texture2 = new Texture("textures/temp_file_2.png");
		spriteBatch = new SpriteBatch();
		showG = false;
		showA = false;
		
		ui = new Stage();
		final Skin skin = SkinManager.getSkin();
		
		Table table = new Table();
		table.setFillParent(true);
		
		table.add(new Label("Join", skin.get(LabelStyle.class))).colspan(2);

		table.row().center();

		table.add(new Label("Name:", skin.get(LabelStyle.class)));

		nameField = new TextField("", skin.get(TextFieldStyle.class));
		nameField.setMessageText("Name");
		nameField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				name = nameField.getText();
				if(name.equalsIgnoreCase("Ganuelas")) {
					showG = true;
				} else {
					showG = false;
				}
				if(name.equalsIgnoreCase("Aki")) {
					showA = true;
				} else {
					showA = false;
				}
			}
		});
		table.add(nameField).fill();

		table.row().expand().center();
		
		serverTable = new Table();
		
		table.add(serverTable).colspan(2);
		
		table.row().bottom().center();
		
		TextButton backButton = new TextButton("Back", skin.get(TextButtonStyle.class));
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				JoinScreen.this.game.setScreen(new MainMenuScreen(JoinScreen.this.game));
				WifiHelper.closeClient();
				dispose();
			}
		});
		table.add(backButton);

		TextButton refreshButton = new TextButton("Refresh", skin.get(TextButtonStyle.class));
		refreshButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				refreshServerList();
			}
		});
		table.add(refreshButton);
		
		ui.addActor(table);
		
		client = WifiHelper.openClient();
		listener = new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof NameResponse) {
					NameResponse response = (NameResponse)object;
					if(response.getRequestId() == currentRequestId) {
						int index = Math.min(response.getIndex(), serverNames.size());
						serverNames.add(index, response.getName());
						serverTable.clear();
						for(int i = 0; i < serverNames.size() && i < addresses.size(); i++) {
							TextButton serverButton = new TextButton(serverNames.get(i), skin.get(TextButtonStyle.class));
							final InetAddress address = addresses.get(i);
							serverButton.addListener(new ClickListener() {
								@Override
								public void clicked(InputEvent event, float x, float y) {
									JoinScreen.this.game.setScreen(new PartyScreen(JoinScreen.this.game, address, name));
									dispose();
								}
							});
							serverTable.add(serverButton);
							serverTable.row();
						}
					}
				}
				synchronized(client) {
					client.notifyAll();
				}
			}
		};
		client.addListener(listener);
	}
	
	/**
	 * Refreshes the list of available servers.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Server list refreshed.
	 */
	private void refreshServerList() {
		currentRequestId++;
		serverNames.clear();
		serverTable.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				addresses = client.discoverHosts(WifiHelper.UDP_PORT, 1000);
				int removed = 0;
				for(int i = 0; i + removed < addresses.size(); i++) {
					synchronized(client) {
						try {
							client.connect(1000, addresses.get(i - removed), WifiHelper.TCP_PORT, WifiHelper.UDP_PORT);
							client.sendTCP(new NameRequest(currentRequestId, addresses.size()));
							try {
								client.wait(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} catch (IOException e) {
							addresses.remove(i);
							removed++;
						}
					}
				}
			}
		}).start();
	}

	/**
	 * Renders the join screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is rendered.
	 */
	@Override
	public void render(float delta) {
		background.render(camera);
		ui.act();
		ui.draw();
		if(showG) {
			spriteBatch.begin();
			spriteBatch.draw(texture, 0, 0);
			spriteBatch.end();
		}
		if(showA) {
			spriteBatch.begin();
			spriteBatch.draw(texture2, 0, 0);
			spriteBatch.end();
		}
	}

	/**
	 * Called when the screen is resized.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is resized.
	 */
	@Override
	public void resize(int width, int height) {
		ui.setViewport(width, height);
		camera.setToOrtho(false);
	}

	/**
	 * Called when the join screen is first shown.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is registered.
	 */
	@Override
	public void show() {
		refreshServerList();
		((InputMultiplexer)Gdx.input.getInputProcessor()).addProcessor(ui);
	}

	/**
	 * Called when the join screen is hidden.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is unregistered.
	 */
	@Override
	public void hide() {
		((InputMultiplexer)Gdx.input.getInputProcessor()).removeProcessor(ui);
	}

	/**
	 * Called when the screen is paused.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void pause() {
	}

	/**
	 * Called when the screen is resumed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void resume() {
	}

	/**
	 * Called when the screen is disposed.
	 * 
	 * pre:
	 * The screen is not disposed.
	 * post:
	 * The screen is disposed.
	 */
	@Override
	public void dispose() {
		background.dispose();
		texture.dispose();
		texture2.dispose();
		spriteBatch.dispose();
		client.removeListener(listener);
		ui.dispose();
	}

}
