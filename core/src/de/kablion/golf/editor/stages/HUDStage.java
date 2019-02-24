package de.kablion.golf.editor.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.golf.actors.entities.Entity;
import de.kablion.golf.data.MapData;
import de.kablion.golf.data.actors.*;
import de.kablion.golf.editor.Application;
import de.kablion.golf.utils.NumbersOnlyFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static de.kablion.golf.utils.Constants.*;

public class HUDStage extends Stage {

    /**
     * Stage that handles and represents the overlay of the Application
     */

    private Application app;
    private WorldStage worldStage;
    private Skin menuSkin = new Skin();

    private long nextSaveAt = 0;
    private int saveEvery = 60*5; // seconds

    private Table rootTable = new Table();

    private ScrollPane menuScroll;

    private Tree menuTree;
    private Table entityMenu = new Table();

    private SelectBox selectEntityTypeBox;

    private Label statusLabel;

    private TextField fileNameField;
    private SelectBox<FileHandle> mapsSelectBox;
    private TextField mapNameField;

    private TextField posXField;
    private TextField posYField;
    private TextField cameraDimensionsField;

    private List<Entity> entityList;

    private Group hudGroup = new Group();

    private Vector2 selectedVertices = null;


    public HUDStage(Application application, WorldStage stage) {
        super(new ExtendViewport(EDITOR_UI_WIDTH, EDITOR_UI_HEIGHT), application.batch);
        this.app = application;
        this.worldStage = stage;


    }

    public void reset() {

        nextSaveAt = System.currentTimeMillis() + saveEvery*1000;

        initSkins();
        initRoot();
        initMenu();
        initHUD();

        updateCameraMenu();
        mapNameField.setText(getMapData().name);
    }

    private void initSkins() {
        menuSkin.addRegions(app.assets.get(EDITOR_SKIN_PATH+".atlas", TextureAtlas.class));
        menuSkin.load(Gdx.files.internal(EDITOR_SKIN_PATH+".json"));
    }

    private void initRoot() {
        clear();
        rootTable.clear();
        rootTable.setFillParent(true);
        addActor(rootTable);
    }

    private void initMenu() {
        Table menuTable = new Table();
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(0,0,0,0.8f);
        p.drawPixel(0, 0);
        menuTable.setBackground(new SpriteDrawable(new Sprite(new Texture(p))));
        rootTable.add(menuTable).left().fill().expandY().width(EDITOR_UI_WIDTH/4f);
        menuTree = new Tree(menuSkin);
        menuScroll = new ScrollPane(menuTree,menuSkin);

        menuScroll.addListener(new ClickListener() {
                        @Override
                        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                            super.enter(event, x, y,pointer,fromActor);
                            // Scroll Focus to WorldStage
                            if(fromActor == null && event.getStage().getScrollFocus() == null) {
                                event.getStage().setScrollFocus(null);
                                event.getStage().setScrollFocus(event.getListenerActor());
                            }

                        }

                        @Override
                        public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
                            super.exit(event, x, y,pointer,toActor);
                            // Scroll Focus to HUDMenu
                            if(toActor == null) {
                                event.getStage().setScrollFocus(null);
                            }
                        }
                    }
        );

        initStatusBar();
        menuTable.add(statusLabel).expandX().fill().pad(20);
        menuTable.row();
        menuTable.add(initShowChangesButton()).expandX().fill().pad(5);
        menuTable.row();
        menuTable.add(menuScroll).expand().fill();
        initMapMenu();
        initEntityList();
        initCameraMenu();
        initEntitySettings();
    }

    private void initStatusBar() {
        //Status Bar (response to the last action)
        statusLabel = new Label("Response to the last action",menuSkin);
        statusLabel.setColor(Color.GREEN);
        statusLabel.setAlignment(Align.center);
    }

    private TextButton initShowChangesButton() {
        //Show Changes Button
        TextButton showChangesButton = new TextButton("Show Changes",menuSkin);
        showChangesButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       super.clicked(event, x, y);
                                       showChanges();
                                   }
                               }
        );
        showChangesButton.getLabel().setFontScale(1f);
        return showChangesButton;
    }

    private void initMapMenu() {
        Table mapMenu = new Table();
        addToMenuTree("General Map Settings", mapMenu);
        menuTree.expandAll();

        //Map Buttons
        Table mapActionsTable = new Table();
        mapMenu.add(mapActionsTable);

        // Map List
        mapsSelectBox = new SelectBox<FileHandle>(menuSkin);
        FileHandle[] mapFiles = MapData.getMaps(true);
        Array<FileHandle> mapFileNames = new Array<FileHandle>();
        for(FileHandle mapFile : mapFiles) {
            if(mapFile.extension().equals("json")) {
                mapFileNames.add(mapFile);
            }
        }
        mapsSelectBox.setItems(mapFileNames);
        mapActionsTable.add(mapsSelectBox).expandX().fill().width(150);

        //Load Map
        TextButton loadButton = new TextButton("Load",menuSkin);
        loadButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       super.clicked(event, x, y);
                                       loadMap();
                                   }
                               }
        );
        loadButton.getLabel().setFontScale(0.75f);
        mapActionsTable.add(loadButton);
        mapActionsTable.row();

        // FileName
        Table fileNameGroup = new Table();
        Label fileNameLabel = new Label("Filename: ",menuSkin);
        fileNameGroup.add(fileNameLabel);
        fileNameField = new TextField("",menuSkin);
        fileNameGroup.add(fileNameField).width(100);
        mapActionsTable.add(fileNameGroup).expandX().fill();
        //Save Map
        TextButton saveButton = new TextButton("Save",menuSkin);
        saveButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       super.clicked(event, x, y);
                                       saveMap();
                                   }
                               }
        );
        saveButton.getLabel().setFontScale(0.75f);
        mapActionsTable.add(saveButton);

        //Map Settings
        mapMenu.row();
        Table inputFieldsTable = new Table();
        mapMenu.add(inputFieldsTable);

        //List of Maps
        /*List entityList = new List(menuSkin);
        ScrollPane entityListScroll = new ScrollPane(entityList,menuSkin);
        entityList.setItems(worldStage.getWorld().getChildren());
        entityListTable.add(entityListScroll).expandX().height(UI_HEIGHT/8).fill();*/
        //File Name
        //Map Name
        inputFieldsTable.row();
        Label mapNameLabel = new Label("Mapname: ",menuSkin);
        inputFieldsTable.add(mapNameLabel);
        mapNameField = new TextField("",menuSkin);
        mapNameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getMapData().name = ((TextField)actor).getText();
            }
        });
        inputFieldsTable.add(mapNameField);
    }

    private void initEntityList() {
        //Entity List
        Table entityListTable = new Table();
        addToMenuTree("List of Entities",entityListTable);

        //Add Entity
        Table addForm = new Table();
        selectEntityTypeBox = new SelectBox(menuSkin);
        try {
            selectEntityTypeBox.setItems(EntityData.EntityType.values());
        } catch (Exception e) {
            Gdx.app.error("Error:", e.getMessage());
        }
        addForm.add(selectEntityTypeBox).fill().expandX().pad(10);

        TextButton addButton = new TextButton("Add",menuSkin);
        addButton.addListener(new ClickListener() {
                                  @Override
                                  public void clicked(InputEvent event, float x, float y) {
                                      super.clicked(event, x, y);
                                      addEntity();
                                  }
                              }
        );
        addButton.getLabel().setFontScale(0.75f);
        addForm.add(addButton);
        entityListTable.add(addForm);
        entityListTable.row();


        //List
        entityList = new List<Entity>(menuSkin);
        ScrollPane entityListScroll = new ScrollPane(entityList,menuSkin);
        entityListScroll.addListener(new ClickListener() {
                                   @Override
                                   public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                                       super.enter(event, x, y,pointer,fromActor);
                                       // Scroll Focus to WorldStage
                                       unfocus(menuScroll);

                                   }

                                   @Override
                                   public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
                                       super.exit(event, x, y,pointer,toActor);
                                       // Scroll Focus to HUDMenu
                                       event.getStage().setScrollFocus(menuScroll);
                                   }
                               }
        );
        entityListScroll.setFadeScrollBars(false);

        entityList.setItems(worldStage.getWorld().getChildren());
        entityList.getItems().reverse();
        entityList.getSelection().setMultiple(false);
        entityList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedVertices = null;
                updateEntitySettings();
            }
        });
        entityListTable.add(entityListScroll).expandX().height(UI_HEIGHT/8f).fill();

        //Entity List Actions
        entityListTable.row();
        HorizontalGroup entityListActionsHGroup = new HorizontalGroup();
        entityListTable.add(entityListActionsHGroup);
        //Move Up
        TextButton upButton = new TextButton("Up",menuSkin);
        upButton.addListener(new ClickListener() {
                                     @Override
                                     public void clicked(InputEvent event, float x, float y) {
                                         super.clicked(event, x, y);
                                         moveEntity(true);
                                     }
                                 }
        );
        upButton.getLabel().setFontScale(0.75f);
        entityListActionsHGroup.addActor(upButton);

        //Remove Selected
        TextButton removeButton = new TextButton("Remove",menuSkin);
        removeButton.addListener(new ClickListener() {
                                     @Override
                                     public void clicked(InputEvent event, float x, float y) {
                                         super.clicked(event, x, y);
                                         removeEntity();
                                     }
                                 }
        );
        removeButton.getLabel().setFontScale(0.75f);
        entityListActionsHGroup.addActor(removeButton);

        //Move Down
        TextButton downButton = new TextButton("Down",menuSkin);
        downButton.addListener(new ClickListener() {
                                 @Override
                                 public void clicked(InputEvent event, float x, float y) {
                                     super.clicked(event, x, y);
                                     moveEntity(false);
                                 }
                             }
        );
        downButton.getLabel().setFontScale(0.75f);
        entityListActionsHGroup.addActor(downButton);
    }

    private void initCameraMenu() {
        Table cameraMenu = new Table();
        addToMenuTree("Camera Settings", cameraMenu);

        //Position X
        final Label posXLabel = new Label("X-Position: ",menuSkin);
        cameraMenu.add(posXLabel);
        posXField = new TextField("",menuSkin);
        posXField.setTextFieldFilter(new NumbersOnlyFilter());
        posXField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = ((TextField)actor).getText();
                float number = 0;
                try {
                    number = Float.parseFloat(text);
                    getMapData().camera.position.x = number;
                } catch (RuntimeException e) {
                    statusLabel.setText("Entered Text cannot be translated to a number.");
                }
            }
        });
        cameraMenu.add(posXField).width(70);
        //Position Y
        cameraMenu.row();
        Label posYLabel = new Label("Y-Position: ",menuSkin);
        cameraMenu.add(posYLabel);
        posYField = new TextField("",menuSkin);
        posYField.setTextFieldFilter(new NumbersOnlyFilter());
        posYField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = ((TextField)actor).getText();
                float number = 0;
                try {
                    number = Float.parseFloat(text);
                    getMapData().camera.position.y = number;
                } catch (RuntimeException e) {
                    statusLabel.setText("Entered Text cannot be translated to a number.");
                }
            }
        });
        cameraMenu.add(posYField).width(70);
        //camera dimension
        cameraMenu.row();
        final Label cameraDimensionsLabel = new Label("Initial Zoom (cm/Display width): ",menuSkin);
        cameraMenu.add(cameraDimensionsLabel);
        cameraDimensionsField = new TextField("",menuSkin);
        cameraDimensionsField.setTextFieldFilter(new NumbersOnlyFilter());
        cameraDimensionsField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = ((TextField)actor).getText();
                float number = 0;
                try {
                    number = Float.parseFloat(text);
                    getMapData().camera.cmPerDisplayWidth = number;
                } catch (RuntimeException e) {
                    statusLabel.setText("Entered Text cannot be translated to a number.");
                }
            }
        });
        cameraMenu.add(cameraDimensionsField).width(70);


        //Get current Camera Button
        cameraMenu.row();
        TextButton getCameraButton = new TextButton("Get Current Camera",menuSkin);
        getCameraButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       super.clicked(event, x, y);
                                       OrthographicCamera cam = (OrthographicCamera) worldStage.getCamera();
                                       int cmPerDisplayWidth = (int)(CameraData.DEFAULT_CMPERDISPLAYWIDTH *((OrthographicCamera) worldStage.getCamera()).zoom);

                                       getMapData().camera.position.x = cam.position.x;
                                       getMapData().camera.position.y = cam.position.y;
                                       getMapData().camera.cmPerDisplayWidth = cmPerDisplayWidth;
                                       updateCameraMenu();
                                   }
                               }
        );
        getCameraButton.getLabel().setFontScale(0.75f);
        cameraMenu.add(getCameraButton);
    }

    private void updateCameraMenu() {
        posXField.setText(""+(int)getMapData().camera.position.x);
        posYField.setText(""+(int)getMapData().camera.position.y);
        cameraDimensionsField.setText(""+(int)getMapData().camera.cmPerDisplayWidth);
    }

    private void initEntitySettings() {
        addToMenuTree("Selected Entity Settings", entityMenu);
        updateEntitySettings();
    }

    public void updateEntitySettings() {
        //Strategie Überlegen für Interconnection
        entityMenu.clearChildren();

        String name = getSelectedEntity().getClass().getSimpleName();
        final Label label = new Label(name+": ",menuSkin);
        label.setFontScale(1.25f);
        entityMenu.add(label);
        entityMenu.row();

        Field[] fields = getSelectedEntity().entityData.getClass().getFields();
        cycleThroughFieldsOfEntity(fields, getSelectedEntity().entityData);
    }

    private void cycleThroughFieldsOfEntity(Field[] fields, Object object) {
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()))
                if (!Modifier.isStatic(field.getModifiers()))
                    if (field.getType().isPrimitive() || field.getType().isArray()) {
                        createTextFieldInEntityMenu(field, object);
                        entityMenu.row();
                    } else {
                        String name = field.getName();
                        final Label label = new Label(name + ": ", menuSkin);
                        entityMenu.add(label);
                        entityMenu.row();
                        try {
                            Field[] subFields = field.getType().getFields();
                            Object subobject = field.get(object);
                            if (subobject != null) {
                                cycleThroughFieldsOfEntity(subFields, subobject);
                            }
                        } catch (IllegalAccessException e) {
                            statusLabel.setText("IllegalAccessException.");
                        }
                    }
        }
    }

    ////////////////////////////////////// To be done!!!
    private void createTextFieldInEntityMenu (final Field field, final Object object) {
        //Label
        String name = field.getName();
        final Label label = new Label(name+": ",menuSkin);
        entityMenu.add(label);
        if(field.getType() == float.class) {
            TextField textField = new TextField("", menuSkin);
            textField.setTextFieldFilter(new NumbersOnlyFilter());
            textField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String text = ((TextField) actor).getText();
                    try {
                        float number = Float.parseFloat(text);
                        try {
                                field.setFloat(object, number);
                                // showChanges();
                        } catch (IllegalAccessException e) {
                            statusLabel.setText("IllegalAccessException.");
                        }
                    } catch (RuntimeException e) {
                        statusLabel.setText("Entered Text cannot be translated to a number.");
                    }
                }
            });
            try {
                    float value = field.getFloat(object);
                    textField.setText("" + value);
            } catch (IllegalAccessException e) {
                statusLabel.setText("IllegalAccessException.");
            }
            entityMenu.add(textField).width(70);
        } else if (field.getType() == boolean.class) {
            CheckBox checkBox = new CheckBox("", menuSkin);
            checkBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                            field.setBoolean(object, ((CheckBox) actor).isChecked());
                            // showChanges();
                    } catch (IllegalAccessException e) {
                        statusLabel.setText("IllegalAccessException.");
                    }
                }
            });
            try {
                    checkBox.setChecked(field.getBoolean(object));
            } catch (IllegalAccessException e) {
                statusLabel.setText("IllegalAccessException.");
            }
            entityMenu.add(checkBox).width(70);
        } else if(field.getType().isArray()) {
            if(field.getName().equals("vertices")) {
                try {
                    float[] verts = (float[]) field.get(object);
                    // Create List of X,Y with add, remove, move
                    createXYList(verts, object, field);
                } catch (IllegalAccessException e) {
                    statusLabel.setText("IllegalAccessException.");
                }
            }
        }
    }

    private void createXYList(final float[] verts, final Object object, final Field field) {
        //XY List
        Table xyListTable = new Table();
        entityMenu.row();
        entityMenu.add(xyListTable).padBottom(10);

        //Add XY
        Table addForm = new Table();
        Label xLabel = new Label("X: ",menuSkin);
        addForm.add(xLabel);
        final TextField xField = new TextField("", menuSkin);
        addForm.add(xField).width(70);

        TextButton changeButton = new TextButton("Change",menuSkin);
        changeButton.getLabel().setFontScale(0.75f);
        addForm.add(changeButton);

        addForm.row();

        Label yLabel = new Label("Y: ",menuSkin);
        addForm.add(yLabel);
        final TextField yField = new TextField("", menuSkin);
        addForm.add(yField).width(70);
        //addForm.row();


        TextButton addButton = new TextButton("Add New",menuSkin);
        addButton.getLabel().setFontScale(0.75f);
        addForm.add(addButton);
        xyListTable.add(addForm);
        xyListTable.row();


        //List
        final List<Vector2> xyList = new List<Vector2>(menuSkin);
        ScrollPane xyListScroll = new ScrollPane(xyList,menuSkin);
        xyListScroll.setFadeScrollBars(false);
        xyListScroll.addListener(new ClickListener() {
                                         @Override
                                         public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                                             super.enter(event, x, y,pointer,fromActor);
                                             // Scroll Focus to WorldStage
                                             unfocus(menuScroll);

                                         }

                                         @Override
                                         public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
                                             super.exit(event, x, y,pointer,toActor);
                                             // Scroll Focus to HUDMenu
                                             event.getStage().setScrollFocus(menuScroll);
                                         }
                                     }
        );

        if(verts != null) {
            for (int i = 0; i < verts.length; i += 2) {
                xyList.getItems().add(new Vector2(verts[i], verts[i + 1]));
            }
            if(xyList.getItems().size > 0) xyList.setSelectedIndex(0);
        }
        xyList.getSelection().setMultiple(false);
        xyList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedVertices = xyList.getSelected();
                xField.setText(""+selectedVertices.x);
                yField.setText(""+selectedVertices.y);
            }
        });

        selectedVertices = xyList.getSelected();
        if(selectedVertices != null) {
            xField.setText(""+selectedVertices.x);
            yField.setText(""+selectedVertices.y);
        }
        xyListTable.add(xyListScroll).expandX().height(UI_HEIGHT/15f).fill();

        //Entity List Actions
        xyListTable.row();
        HorizontalGroup entityListActionsHGroup = new HorizontalGroup();
        xyListTable.add(entityListActionsHGroup);

        // Change
        changeButton.addListener(new ClickListener() {
                                  @Override
                                  public void clicked(InputEvent event, float x, float y) {
                                      super.clicked(event, x, y);
                                      // Add XY
                                      try {
                                          if(selectedVertices != null) {
                                              float xValue = Float.parseFloat(xField.getText());
                                              float yValue = Float.parseFloat(yField.getText());
                                              selectedVertices.x = xValue;
                                              selectedVertices.y = yValue;
                                              xyListChanged(xyList, field, object);
                                          }
                                      } catch (RuntimeException e) {
                                          statusLabel.setText("Entered Text cannot be translated to a number.");
                                      }
                                  }
                              }
        );

        // Add
        addButton.addListener(new ClickListener() {
                                  @Override
                                  public void clicked(InputEvent event, float x, float y) {
                                      super.clicked(event, x, y);
                                      // Add XY
                                      try {
                                          float xValue = Float.parseFloat(xField.getText());
                                          float yValue = Float.parseFloat(yField.getText());
                                          xyList.getItems().add(new Vector2(xValue,yValue));
                                          xyListChanged(xyList, field, object);
                                      } catch (RuntimeException e) {
                                          statusLabel.setText("Entered Text cannot be translated to a number.");
                                      }
                                  }
                              }
        );

        //Move Up
        TextButton upButton = new TextButton("v",menuSkin);
        upButton.addListener(new ClickListener() {
                                 @Override
                                 public void clicked(InputEvent event, float x, float y) {
                                     super.clicked(event, x, y);
                                     // Move Up
                                     if(selectedVertices != null) {
                                         int index = xyList.getItems().indexOf(selectedVertices, true);
                                         if (index < xyList.getItems().size - 1)
                                             xyList.getItems().swap(index, index + 1);
                                         xyListChanged(xyList, field, object);
                                     }
                                 }
                             }
        );
        upButton.getLabel().setFontScale(0.75f);
        entityListActionsHGroup.addActor(upButton);

        //Remove Selected
        TextButton removeButton = new TextButton("-",menuSkin);
        removeButton.addListener(new ClickListener() {
                                     @Override
                                     public void clicked(InputEvent event, float x, float y) {
                                         super.clicked(event, x, y);
                                         // remove
                                         if(selectedVertices != null) {
                                             xyList.getItems().removeValue(selectedVertices, true);
                                             selectedVertices = null;
                                             xyListChanged(xyList, field, object);
                                         }
                                     }
                                 }
        );
        removeButton.getLabel().setFontScale(0.75f);
        entityListActionsHGroup.addActor(removeButton);

        //Move Down
        TextButton downButton = new TextButton("^",menuSkin);
        downButton.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       super.clicked(event, x, y);
                                       // Move Down
                                       if(selectedVertices != null) {
                                           int index = xyList.getItems().indexOf(selectedVertices, true);
                                           if (index > 0) xyList.getItems().swap(index, index - 1);
                                           xyListChanged(xyList, field, object);
                                       }
                                   }
                               }
        );
        downButton.getLabel().setFontScale(0.75f);
        entityListActionsHGroup.addActor(downButton);
    }

    private void xyListChanged(List<Vector2> xyList, Field field, Object object) {
        Array<Vector2> vectors = xyList.getItems();
        float[] newVerts = new float[vectors.size*2];
        int indexList = 0;
        for(int i=0; i<newVerts.length;i+=2) {
            Vector2 v = vectors.get(indexList);
            newVerts[i] = v.x;
            newVerts[i+1] = v.y;
            indexList++;
        }
        try {
            field.set(object, newVerts);
        } catch (Exception e) {
            Gdx.app.error("Error:", e.getMessage());
        }
        showChanges();
    }

    private void addToMenuTree(String title, Actor actor) {
        Tree.Node actorNode = new Tree.Node(new Label(title, menuSkin));
        menuTree.add(actorNode);
        actorNode.add(new Tree.Node(actor));
    }

    private void loadMap() {
        try {
            worldStage.getWorld().setMapData(MapData.loadMap(mapsSelectBox.getSelected()));
            showChanges();
            mapNameField.setText(getMapData().name);
            updateCameraMenu();
        } catch (IllegalArgumentException e) {
            statusLabel.setColor(Color.YELLOW);
            statusLabel.setText(e.getMessage());
        }
        setMessage("Map " + fileNameField.getText() + " loaded");
    }

    private void saveMap() {
        String fileName = fileNameField.getText();
        if(fileName.equals("")) {
            setMessage("Map Filename is Empty");
        } else {
            MapData.saveMap(getMapData(), fileNameField.getText());
            setMessage("Map " + fileNameField.getText() + " saved");
        }
    }

    private void showChanges() {
        try {
            selectedVertices = null;
            EntityData entityData = getSelectedEntity().entityData;
        worldStage.loadMap();
        entityList.setItems(worldStage.getWorld().getChildren());
        // select the same Entity as before
            Array<Entity> entities = entityList.getItems();
            entities.reverse();
        for (int i = 0; i<entities.size; i++) {
            Entity entity = entities.get(i);
            if (entity.entityData == entityData) {
                entityList.setSelected(entity);
            }
        }
        updateEntitySettings();
        } catch (IllegalArgumentException e) {
            statusLabel.setColor(Color.YELLOW);
            statusLabel.setText(e.getMessage());
        }
    }

    private void addEntity() {
        EntityData.EntityType entityType = (EntityData.EntityType) selectEntityTypeBox.getSelected();
        EntityData entity = null;
        if(getSelectedEntity().entityData.getType() == entityType && entityType != EntityData.EntityType.BALL) {
            entity = getSelectedEntity().entityData.clone();
        } else {
            switch (entityType) {
                case HOLE:
                    entity = new HoleData();
                    break;
                case WALL:
                    entity = new WallData();
                    break;
                case GROUND:
                    entity = new GroundData();
                    break;
                case MAGNET:
                    entity = new MagnetData();
                    break;
                case BALL:
                    setMessage("Ball can not be added.");
                default:
                    break;
            }
        }
        if(entity != null) {
            getMapData().entities.add(entity);
            showChanges();
            entityList.setSelectedIndex(1);
            setMessage(entityType + " Created");
        }
    }

    private void removeEntity() {
        getMapData().entities.removeValue(entityList.getSelected().entityData, true);
        showChanges();

        setMessage("Entity Removed");
    }

    private void moveEntity(boolean up) {
        int index = getMapData().entities.indexOf(getSelectedEntity().entityData, true);
        if (up && index<getMapData().entities.size-1) getMapData().entities.swap(index, index+1);
        else if (!up && index>0) getMapData().entities.swap(index, index-1);
        showChanges();
        setMessage("Entity successfully moved.");
    }

    private void setMessage(String string) {
        statusLabel.setText(string);
    }

    private void initHUD() {
        rootTable.add(hudGroup).expand();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Save map automatically
        if(System.currentTimeMillis()>nextSaveAt) {
            String fileName = fileNameField.getText();
            String mapName = getMapData().name;
            getMapData().name = "[AUTO SAVED] "+mapName;
            fileNameField.setText("autosaved");
            saveMap();
            fileNameField.setText(fileName);
            getMapData().name = mapName;
            setMessage("Saved Map Automatically");

            nextSaveAt = System.currentTimeMillis() + saveEvery*1000;
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        clear();
        menuSkin.dispose();
        worldStage = null;
    }

    public Entity getSelectedEntity() {
        return entityList.getSelected();
    }

    public void setSelectedEntity(Entity entity) {
        this.entityList.setSelected(entity);
    }

    public Vector2 getSelectedVertices() {
        return this.selectedVertices;
    }

    private MapData getMapData() {
        return worldStage.getWorld().getMapData();
    }
}
