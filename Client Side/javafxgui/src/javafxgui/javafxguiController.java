package javafxgui;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class javafxguiController extends ClientInformation implements Initializable, ClientActionListener, NetConnListener {
    
    private final String IP_ADDR = "192.168.0.100";
    String myPath = "";
    private final int PORT = 3333;
    private NetConnect netcon;
    private boolean isTrue = true, trigerList = true, isFalse = false, settingsListTriger = false, showTriger = false;
    private double Rightprefwidth = 0.0d, Leftprefwidth = 0.0d;
    private String FilePath, FileExtention = "";
    private File imageFile;
    public final String fileExtntion = FileExtention;
    private Alert alert = new Alert(AlertType.CONFIRMATION);
    private final static FileChooser fileChooser = new FileChooser();
    Clipboard systemClipboard = Clipboard.getSystemClipboard();
    DropShadow shadowEffect = new DropShadow();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final Tooltip LoginHint1 = new Tooltip("Логін це почта яку ви вказали при реєстрації");
    private final Tooltip LoginHint2 = new Tooltip("Пароль вашого акаунту який ви вводили при реєстрації");
    private final Tooltip RegHint1 = new Tooltip("Вкажіть існуючу почту до якої у вас є доступ");
    private final Tooltip RegHint2 = new Tooltip("Вкажіть ваше ім'я");
    private final Tooltip RegHint3 = new Tooltip("Пароль має бути не меньше 6 символів і складатись хотя б з одної цифри");
    private final ObservableList<String> list
            = FXCollections.observableArrayList(
                    "@gmail.com",
                    "@ukr.net",
                    "@yahoo.com"
            );
    private final ObservableList<String> listGender
            = FXCollections.observableArrayList(
                    "Чоловік",
                    "Жінка"
            );
    private final ObservableList<String> countries = FXCollections.observableArrayList();
    private final ObservableList<Integer> listDay = FXCollections.observableArrayList();
    private final ObservableList<Integer> listYear = FXCollections.observableArrayList();
    private final ObservableList<Integer> listMonth = FXCollections.observableArrayList();
    private ContextMenu popMenu = new ContextMenu();
    private MenuItem exitItem = new MenuItem("Вийти");
    private MenuItem settingsItem = new MenuItem("Налаштування");
    private MenuItem changeAcc = new MenuItem("Змінити акаунт");
    private ContextMenu fileMenu = new ContextMenu();
    private MenuItem imageChoose = new MenuItem("Зображення");
    private MenuItem fileChoose = new MenuItem("Файл");
    private MenuItem audioChoose = new MenuItem("Аудіо");
    private ContextMenu dialogSettings = new ContextMenu();
    private MenuItem deleteDialog = new MenuItem("Видалити мої повідомлення(для двох)");
    private MenuItem deleteFriend = new MenuItem("Видалити друга");
    private MenuItem deleteDialogForMe = new MenuItem("Видалити мої повідомлення(для себе)");
    private ContextMenu deleteMessages = new ContextMenu();
    private MenuItem deleteMessage = new MenuItem("Видалити повідомлення(для двох)");
    private MenuItem deleteMessageForme = new MenuItem("Видалити повідомлення");
    private MenuItem addFriend = new MenuItem("Прийняти заявку в друзі");
    private MenuItem copyMessage = new MenuItem("Копіювати повідомлення");
    private MenuItem openInDir = new MenuItem("Відкрити папку");
    private MenuItem openFile = new MenuItem("Відкрити");
    private MenuItem download = new MenuItem("Завантажити");
    private Alert confirmAlert = new Alert(AlertType.CONFIRMATION);

    @FXML
    private TextField SendText, RegNameText, RegLogText, RegPassText, RegLastName;
    @FXML
    private Label LabelRegHint, LabelHint, RegHint, FirstLoginHint, SecondLoginHint,
            FirstRegHint, SecondRegHint, ThirdRegHint, LabelWho, Status,
            LabelSecondName, LabelName, NameShow, SecondNameShow, BirthdayShow, GenderShow, CountryShow,
            changePasswordError;
    @FXML
    private TextField LoginText, PathTextField, SearchTextField, SetingName, SetingSurname, OldPassword, NewPassword, 
            RepeateOldPassword;
    @FXML
    private PasswordField PassText;
    @FXML
    private AnchorPane AnchorPaneLogin, AnchorOption, AncorProfile;
    @FXML
    private CheckBox CheckMale, CheckFamale;
    @FXML
    private AnchorPane AnchorPaneRegist, ShowInfoPane;
    @FXML
    private ComboBox<String> ComboMail, ComboReg, ComboRegGender, ComboCountry, ComboGender;
    @FXML
    private ComboBox<Integer> ComboRegDay, ComboRegYear, ComboRegMonth, ComboDay, ComboMonth, ComboYear;
    @FXML
    private ListView ListUsers, MessageList;
    @FXML
    private ImageView ViewPhoto, MyFoto, SettingsImage, LogImg1,
            LogImg2, LogImg3, LogImg4, LogImg5, LogImg6, LogImg7, LogImg8,
            BorderImg1, BorderImg2, BorderImg3, BorderImg4, ImageShow, logoImage;
    @FXML
    private Button fileChooseButton, SendButton, AddFriendBtnShow, logoButton;
    @FXML
    private VBox mainbox, LeftImage, RightImage, CenterVbox, LeftMessage, RightMessage, LeftVbox, RightVbox;
    @FXML
    private BorderPane Split;
    @FXML
    private TextArea InfoShow, SettingsInfo;
    @FXML
    private AnchorPane AnchorFiles, AnchorAudio, AnchorImage, AcncorSearch, HelpPanel;
    @FXML
    private HBox ClientInfoPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (isTrue == true) {
            isFalse = false;
            new CreateDir(myPath);
            try {
                System.out.println("I`m in constructor!");
                netcon = new NetConnect(this, this, IP_ADDR, PORT);
                System.out.println(""+netcon.socket.getLocalSocketAddress().toString());
            } catch (IOException e) {
                try{
                confirmAlert.setHeaderText("");
                confirmAlert.setTitle("Вкажіть ip-адрес сервера");
                confirmAlert.setContentText("Вкажіть ip-адрес сервера");
                TextField ip = new TextField();
                ip.setPromptText("set ip address like: "+IP_ADDR);
                confirmAlert.setGraphic(ip);
                System.out.println("Нема підключення до інтернету");
                Optional<ButtonType> choose = confirmAlert.showAndWait();
                if(choose.get().equals(ButtonType.OK)) {
                    netcon = new NetConnect(this, this, ip.getText(), PORT);
                }
                }catch(IOException ex) {
                System.out.println("IOExc");
                }
            } catch (Exception ex) {
                System.out.println("Помилка в конструкторі");
            }
            AnchorPaneLogin.setVisible(true);
            ComboMail.setItems(list);
            ComboMail.setValue(list.get(0));
            ComboReg.setItems(list);
            ComboReg.setValue(list.get(0));
            FirstLoginHint.setTooltip(LoginHint1);
            SecondLoginHint.setTooltip(LoginHint2);
            FirstRegHint.setTooltip(RegHint1);
            SecondRegHint.setTooltip(RegHint2);
            ThirdRegHint.setTooltip(RegHint3);
            netcon.sendString("TOOL", "");
            for (Integer day = 1; day <= 31; day++) {
                listDay.add(day);
            }
            for (Integer month = 1; month <= 12; month++) {
                listMonth.add(month);
            }
            for (Integer year = 1910; year <= 2017; year++) {
                listYear.add(year);
            }
            ComboRegGender.setItems(listGender);
            ComboRegDay.setItems(listDay);
            ComboRegMonth.setItems(listMonth);
            ComboRegYear.setItems(listYear);
            alert.initModality(Modality.APPLICATION_MODAL);
        }
        if (isTrue == false && isFalse == false) {
            System.out.println("I`m in constructor2!");
            trigerList = true;
            isFalse = true;
            Rightprefwidth = RightVbox.getPrefWidth();
            Leftprefwidth = LeftVbox.getPrefWidth();;
            Platform.runLater(() -> {
                
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                FileChooser.ExtensionFilter extFilterBMP = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.BMP");
                FileChooser.ExtensionFilter extFilterTXT = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.TXT");
                FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.PDF");
                FileChooser.ExtensionFilter extFilterMP3 = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.MP3");
                fileChooser.setTitle("Resource Chooser");
                fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterBMP, extFilterTXT, extFilterPDF, extFilterMP3);
                popMenu.getItems().removeAll(popMenu.getItems());
                popMenu.getItems().addAll(settingsItem, changeAcc, exitItem);
                logoButton.setContextMenu(popMenu);
                netcon.sendString("INFOFRIEND", "");
                netcon.sendString("MYIN", "");
                try {
                    ImageView logo = new ImageView(bytesToImage(ToolsBag.get(9)));
                    logo.setFitHeight(70);
                    logo.setFitWidth(81);
                    logoButton.setGraphic(logo);
                } catch (IOException e) {

                }
                fileMenu.getItems().removeAll(fileMenu.getItems());
                fileMenu.getItems().addAll(imageChoose, fileChoose, audioChoose);

                imageChoose.addEventHandler(ActionEvent.ACTION, event -> {
                    fileChooser.getExtensionFilters().clear();
                    fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterBMP);
                    chooserHandler();
                });

                fileChoose.addEventHandler(ActionEvent.ACTION, event -> {
                    fileChooser.getExtensionFilters().clear();
                    fileChooser.getExtensionFilters().addAll(extFilterTXT, extFilterPDF);
                    chooserHandler();
                });
                audioChoose.addEventHandler(ActionEvent.ACTION, event -> {
                    fileChooser.getExtensionFilters().clear();
                    fileChooser.getExtensionFilters().addAll(extFilterMP3);
                    chooserHandler();
                });

                exitItem.addEventHandler(ActionEvent.ACTION, event -> {
                    alert.setTitle("Вихід з програми");
                    alert.setHeaderText("Ви вийдите з цього акаунту");
                    alert.setContentText("Ви хочите вийти ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        System.exit(0);
                    } else {
                        alert.close();
                    }

                });

                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(copyMessage, deleteMessage, deleteMessageForme);
                deleteDialogForMe.addEventHandler(ActionEvent.ACTION, event1 -> {
                    alert.setTitle("Видалити повідомлення");
                    alert.setHeaderText("Ви видалите всі ваші повідомлення(для себе)");
                    alert.setContentText("Ви хочите видалити всі повідомлення ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        netcon.sendString("DELEMYLS", "" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) + "id");
                    } else {
                        alert.close();
                    }

                });

                copyMessage.addEventHandler(ActionEvent.ACTION, event10 -> {
                    if (MessageText.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                        ClipboardContent content = new ClipboardContent();
                        content.putString(MessageText.get(1 + MessageList.getSelectionModel().getSelectedIndex()));
                        systemClipboard.setContent(content);
                        deleteMessages.getItems().removeAll(deleteMessages.getItems());
                        deleteMessages.getItems().addAll(copyMessage, deleteMessage, deleteMessageForme);

                    }
                });

                deleteDialog.addEventHandler(ActionEvent.ACTION, event2 -> {
                    alert.setTitle("Видалити діалог");
                    alert.setHeaderText("Ви видалите всі ваші повідомлення(для двох)");
                    alert.setContentText("Ви хочите видалити всі повідомлення ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        netcon.sendString("DELEBOLS", "" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) + "id");
                    } else {
                        alert.close();
                    }

                });

                openInDir.addEventHandler(ActionEvent.ACTION, event12 -> {
                    try {
                        Desktop.getDesktop().open(new File(MYPath.get(1)));
                    } catch (IOException ex) {

                    }
                });

                download.addEventHandler(ActionEvent.ACTION, event1245 -> {
                    try {
                        if (MessageFile.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                            deleteMessages.getItems().removeAll(deleteMessages.getItems());
                            deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                            String filePath = myPath
                                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "txt";
                            File file = new File(filePath);

                            deleteMessages.getItems().removeAll(deleteMessages.getItems());
                            deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                            file.createNewFile();
                            FileOutputStream fout = new FileOutputStream(file);
                            fout.write(MessageFile.get(1 + MessageList.getSelectionModel().getSelectedIndex()));

                        } else if (MessageImage.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                            System.out.println("IN FILE");
                            deleteMessages.getItems().removeAll(deleteMessages.getItems());
                            deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                            String filePath = myPath
                                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "png";
                            File file = new File(filePath);
                            System.out.println("MessageImage: " + fileExtntion);
                            InputStream in = new ByteArrayInputStream(MessageImage.get(1 + MessageList.getSelectionModel().getSelectedIndex()));
                            BufferedImage image = ImageIO.read(in);
                            ImageIO.write(image, "png", new File(filePath));
                            System.out.println("File Loaded");

                        } else if (MessageAudio.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                            deleteMessages.getItems().removeAll(deleteMessages.getItems());
                            deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                            String filePath = myPath
                                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "mp3";
                            File AudioFile = new File(filePath);
                            try (FileOutputStream fileOut = new FileOutputStream(AudioFile)) {
                                fileOut.write(MessageAudio.get(1 + MessageList.getSelectionModel().getSelectedIndex()));
                            } catch (Exception e) {
                                System.out.println("File send Exception ");
                            }

                        }

                    } catch (Exception b) {
                        System.out.println("Exception Contetn");
                    }
                });

                openFile.addEventHandler(ActionEvent.ACTION, event444 -> {
                    try {
                        if (MessageImage.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                            String filePath = myPath
                                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "png";
                            Desktop.getDesktop().open(new File(filePath));
                        } else if (MessageFile.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                            String filePath = myPath
                                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "txt";
                            Desktop.getDesktop().open(new File(filePath));

                        } else if (MessageAudio.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
                            String filePath = myPath
                                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "mp3";
                            Desktop.getDesktop().open(new File(filePath));
                        }
                    } catch (Exception ex) {
                        System.out.println("can`t open file MenuItem");
                    }
                });

                deleteFriend.addEventHandler(ActionEvent.ACTION, event3 -> {
                    alert.setTitle("Видалити друга");
                    alert.setHeaderText("Ви видалите друга");
                    alert.setContentText("Ви хочите видалити " + LabelWho.getText() + " з друзів ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        netcon.sendString("DELEMYFR", "" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) + "id");
                    } else {
                        alert.close();
                    }

                });

                deleteMessage.addEventHandler(ActionEvent.ACTION, event3 -> {
                    alert.setTitle("Видалити повідомлення");
                    alert.setHeaderText("Ви видалите повідомлення(для двох)");
                    alert.setContentText("Ви хочите видалити повідомлення ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        netcon.sendString("DELEBOMS", "" + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "id");
                    } else {
                        alert.close();
                    }
                });

                deleteMessageForme.addEventHandler(ActionEvent.ACTION, event3 -> {
                    alert.setTitle("Видалити повідомлення");
                    alert.setHeaderText("Ви видалите повідомлення(для себе)");
                    alert.setContentText("Ви хочите видалити повідомлення?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        netcon.sendString("DELEMYMS", "" + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "id");
                    } else {
                        alert.close();
                    }
                });

                addFriend.addEventHandler(ActionEvent.ACTION, event -> {
                    alert.setTitle("Добавлення друга");
                    alert.setHeaderText("Ви добавлеєте користувача: " + LabelWho.getText());
                    alert.setContentText("Ви хочите добавити друга ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        netcon.sendString("DELEMYLS", "" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) + "id");
                    } else {
                        alert.close();
                    }

                    netcon.sendString("COMD", "CNFID" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                });

                changeAcc.addEventHandler(ActionEvent.ACTION, event -> {
                    alert.setTitle("Змінити акаунт");
                    alert.setHeaderText("Для зміни ви аийдете з цього акаунта");
                    alert.setContentText("Ви хочите змінити акаунт ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        try {
                            
                            new LoginFx().start((Stage) logoButton.getScene().getWindow());
                            logoButton.getScene().getWindow().hide();
                        } catch (Exception e) {

                        }
                    } else {
                        alert.close();
                    }
                });

                settingsItem.addEventHandler(ActionEvent.ACTION, event -> {
                    Platform.runLater(() -> {
                        try {
                            settingsListTriger = true;
                            FXMLLoader loader = new FXMLLoader(javafxguiController.this.getClass().getResource("SettingsFXML.fxml"));
                            loader.setController(javafxguiController.this);
                            new SettingsStage(loader, Status.getScene().getWindow());
                            PathTextField.setText(myPath);
                            SetingSurname.setText(MYLatName.get(1));
                            SettingsImage.setImage(MYFoto.get(1));
                            fillupComboBoxes();
                            String[] userBirthday = MYBirthday.get(1).split("/");
                            ComboDay.setValue(Integer.parseInt(userBirthday[0]));
                            ComboMonth.setValue(Integer.parseInt(userBirthday[1]));
                            ComboYear.setValue(Integer.parseInt(userBirthday[2]));
                            ComboGender.setValue(MYGender.get(1));
                            ComboCountry.setValue(MYCountry.get(1));
                        } catch (IOException e) {
                            System.out.println("Settinds IO error" + Arrays.toString(e.getStackTrace())
                                    + "\n" + e.getMessage());
                        }
                    });
                });

            });
        }
        isTrue = false;
        settingsListTriger = false;
    }

    public void ClientInfoShow() {
        try {
            settingsListTriger = true;
            FXMLLoader loader = new FXMLLoader(javafxguiController.this.getClass().getResource("SettingsFXML.fxml"));
            loader.setController(javafxguiController.this);
            new SettingsStage(loader, Status.getScene().getWindow());
            ClientInfoPane.setVisible(false);
            ShowInfoPane.setVisible(true);
            SettingsInfo.setEditable(false);
            InfoShow.setEditable(false);
            if (showTriger == false) {
                AddFriendBtnShow.setVisible(false);
            } else {
                AddFriendBtnShow.setVisible(true);
            }
            NameShow.setText(SearchName.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
            SecondNameShow.setText(SearchLastName.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
            BirthdayShow.setText(SearchBirthday.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
            GenderShow.setText(SearchGender.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
            CountryShow.setText(SearchCountry.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
            ImageShow.setImage(SearchImage.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
            InfoShow.setText(SearchInformation.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
        } catch (IOException e) {

        }

    }

    @FXML
    public void MessageListClick(MouseEvent event) {
        System.out.println("0");
        if (MessageText.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {

            deleteMessages.getItems().removeAll(deleteMessages.getItems());
            deleteMessages.getItems().addAll(copyMessage, deleteMessage, deleteMessageForme);

        } else if (MessageImage.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
            String filePath = myPath
                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "png";
            System.out.println("FilePath: " + filePath);

            File AudioFile = new File(filePath);
            if (AudioFile.length() > 0) {
                System.out.println("1");
                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);

            } else {
                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(download, deleteMessage, deleteMessageForme);
                System.out.println("2");
            }
        } else if (MessageFile.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
            String filePath = myPath
                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "txt";
            
            File AudioFile = new File(filePath);
            if (!(AudioFile.length() > 0)) {
                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(download, deleteMessage, deleteMessageForme);
            } else {
                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
            }
        } else if (MessageAudio.get(1 + MessageList.getSelectionModel().getSelectedIndex()) != null) {
            String filePath = myPath
                    + MYName.get(1) + MessageID.get(1 + MessageList.getSelectionModel().getSelectedIndex()) + "." + "mp3";
            File AudioFile = new File(filePath);
            if (!(AudioFile.length() > 0)) {
                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(download, deleteMessage, deleteMessageForme);
            } else {
                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
            }
        }
    }

    @FXML
    public void LoginbtnActionHandler(ActionEvent event) {
        Platform.runLater(() -> {
            if (LoginText.getText().equals("") || PassText.getText().equals("")) {
                LoginText.setStyle("-fx-border-color:red");
                PassText.setStyle("-fx-border-color:red");
                LabelHint.setStyle("-fx-text-fill: red");
                LabelHint.setText("Поля логіна та пароля не можуть бути пустими");
                LoginText.clear();
                PassText.clear();
            } else {
                String ipaddress = getIPAddress(netcon.socket.getLocalSocketAddress().toString());
                netcon.sendString("SERV", ipaddress + ""
                        + "UserIPAddressUPDATE users SET UserIPAddress = '"
                        + ipaddress
                        + "' WHERE UserMail = '" + LoginText.getText() + ComboMail.getValue() +
                        "' and UserPassword = '" + PassText.getText() + "'");
                netcon.sendString("SERV", ipaddress
                        + "UserIPAddressSELECT UserID, UserIPAddress FROM users WHERE UserMail = '" +
                        LoginText.getText() + ComboMail.getValue() + "' and UserPassword = '" + PassText.getText() + "'");
                LoginText.clear();
                PassText.clear();
            }
        });
    }
   
    
    @FXML
    public void onEnter(ActionEvent event) {
        if (trigerList) {

        } else {
            if (SearchTextField.getText().equals("")) {
                clearAllUsers();
                netcon.sendString("INFOSEARCH", "");
            } else {
                clearAllUsers();
                netcon.sendString("INFOSEARCH", "and UserName = '" + SearchTextField.getText() + "'");
            }
        }
    }

    @FXML
    public void RegButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            String regNameText = RegNameText.getText();
            String regLogText = RegLogText.getText();
            String regPassText = RegPassText.getText();

            if (regNameText.equals("") || regLogText.equals("") || regPassText.equals("")) {
                RegNameText.setStyle("-fx-border-color:red");
                RegLogText.setStyle("-fx-border-color:red");
                RegPassText.setStyle("-fx-border-color:red");

                RegHint.setText("Поля імені, логіна та пароля не можуть бути пустими");
                RegHint.setStyle("-fx-text-fill: red");
            } else {
                String ipaddress = getIPAddress(netcon.socket.getLocalSocketAddress().toString());
                String insert = ipaddress
                        + "UserIPAddressINSERT INTO charchatdb.users(users.UserName, users.UserLastName, users.UserMail, users.UserPassword, users.UserBirthday, users.UserGender, users.UserIPAddress)"
                        + "values ('" + regNameText + "','" + RegLastName.getText() + "','" + regLogText + ComboReg.getValue() + "','" + regPassText
                        + "','" + ComboRegDay.getValue() + "/" + ComboRegMonth.getValue() + "/" + ComboRegYear.getValue() + "','" + ComboRegGender.getValue() + "','"
                        + ipaddress + "')";
                System.out.println(insert);
                netcon.sendString("SERV", insert);
            }

        });
    }
    
    private String getIPAddress(String string) {
        for(int i = 0, b = 1; i < string.length(); i++, b++) {
            if(string.substring(i, b).equals(":")) {
                return string.substring(0,b-1);
            }
        }
        return "unknown";    
    }
    
    @FXML
    public void ToolSearchUsers(ActionEvent event) {
        trigerList = false;
        Platform.runLater(() -> {
            showTriger = true;
            clearAllUsers();
        });
    }

    @FXML
    public synchronized void printMsg(String value) {
        Platform.runLater(() -> {
            try {
                addToMessages("msgEXTE"+value, 1);
            } catch (IOException e) {
                System.out.println("printMsg IOExeption");
            }
        });
    }

    public void SendButtonHandler(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                if (!SendText.getText().equals("")) {
                    System.out.println("Send Text: " + SendText.getText());
                    if (addToMessages("msgEXTE" + SendText.getText(), 0)) {
                        createMessageList();
                    }
                    System.out.println("CLIE" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex())
                            + "idDate" + dateFormat.format(new Date()) + SendText.getText());
                    netcon.sendString("CLIE", SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex())
                            + "idDate" + dateFormat.format(new Date()) + SendText.getText());
                    SendText.setText("");
                }
            } catch (IOException e) {
                System.out.println("Send message IOException");
            }
        });
    }

    public void chooserHandler() {
        Platform.runLater(() -> {
            try {
                File file = fileChooser.showOpenDialog(LoginText.getScene().getWindow());
                if ((file.exists())) {
                    String plus = "";
                    String Fileextention = file.getName().substring(file.getName().length() - 3);
                        plus = Arrays.toString(java.nio.file.Files.readAllBytes(file.toPath()));

                    netcon.sendString("CLIE", SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex())
                            + "id" + "Date" + dateFormat.format(new Date()) + "FILE" + Fileextention + "FILE" + plus);
                    System.out.println("FileExtention " + FileExtention);
                    if (addToMessages(Fileextention + "EXTE" + plus, 0)) {
                        createMessageList();
                    }
                }

            } catch (IOException e) {
                System.out.println("FileChooser Exception!");
            } 
        });

    }

    @FXML
    public void FileChooseButtonAction(MouseEvent event) {
        fileMenu.show(fileChooseButton, event.getScreenX(), event.getScreenY());
    }

    @FXML
    public void fileAcordioClick(MouseEvent event) {
        Platform.runLater(() -> {

            int size = MessageFile.size();
            for (int i = 1; i <= size; i++) {
                if (MessageFile.get(i) != null) {
                    try {
                        final int a = i;
                        ImageView filefile = new ImageView();
                        filefile.setFitHeight(100);
                        filefile.setFitWidth(100);
                        filefile.setImage(bytesToImage(ToolsBag.get(3)));
                        AnchorFiles.getChildren().add(filefile);
                        filefile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event00) {
                                try {
                                    String filePath = myPath
                                            + MYName.get(1) + MessageID.get(a) + "." + "txt";
                                    File file = new File(filePath);
                                    if (file.length() > 0) {
                                        Desktop.getDesktop().open(new File(filePath));
                                    } else {
                                        file.createNewFile();
                                        FileOutputStream fout = new FileOutputStream(file);
                                        fout.write(MessageFile.get(a));
                                    }
                                } catch (IOException e) {
                                    System.out.println("");
                                }
                            }
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(javafxguiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        });
    }

    @FXML
    public void imageAcordioClick(MouseEvent event) {
        Platform.runLater(() -> {
            try {
                int size = MessageImage.size();
                System.out.println("SIZE: " + size);
                for (int i = 1; i <= size; i++) {
                    if (MessageImage.get(i) != null) {
                        System.out.println("In for ");
                        ImageView fileImage = new ImageView();
                        fileImage.setFitHeight(100);
                        fileImage.setFitWidth(100);
                        fileImage.setImage(bytesToImage(MessageImage.get(i)));
                        AnchorImage.getChildren().add(fileImage);
                    }
                }
            } catch (IOException e) {
                System.out.println("IOExcepiton acordion image");
            }
        });

    }

    @FXML
    public void audioAcordioClick(MouseEvent event) {
        Platform.runLater(() -> {
            try {
                int size = MessageAudio.size();
                for (int i = 1; i <= size; i++) {
                    if (MessageAudio.get(i) != null) {
                        ImageView fileAudio = new ImageView();
                        fileAudio.setFitHeight(100);
                        fileAudio.setFitWidth(100);
                        fileAudio.setImage(bytesToImage(ToolsBag.get(1)));
                        AnchorAudio.getChildren().add(fileAudio);
                    }
                }
            } catch (IOException e) {

            }
        });
    }

    @FXML
    public void ListUsersMouseCliecked(MouseEvent event) {
        Platform.runLater(() -> {
            if (trigerList) {
                clearAllMessages();
                int selectedIndex = 1 + ListUsers.getSelectionModel().getSelectedIndex();
                LabelWho.setText(SearchName.get(selectedIndex) + " " + SearchLastName.get(selectedIndex));
                ViewPhoto.setImage(SearchImage.get(selectedIndex));
                System.out.println("I send: " + "ID" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                netcon.sendString("MSLI", "ID" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                switch (SearchStatus.get(selectedIndex)) {

                    case 0:
                        SendText.setText("Цей користувач ще не прийняв заявку в друзі");
                        SendText.setEditable(false);
                        SendButton.setDisable(true);
                        fileChooseButton.setDisable(true);
                        dialogSettings.getItems().removeAll(dialogSettings.getItems());
                        dialogSettings.getItems().addAll( deleteFriend);
                        break;
                    case 1:
                        SendText.setText("");
                        SendText.setEditable(true);
                        SendButton.setDisable(false);
                        fileChooseButton.setDisable(false);
                        dialogSettings.getItems().removeAll(dialogSettings.getItems());
                        dialogSettings.getItems().addAll(deleteDialogForMe, deleteDialog, deleteFriend);
                        break;

                    case 2:
                        SendText.setText("Ви ще не прийняли заявку в друзі");
                        SendText.setEditable(false);
                        SendButton.setDisable(true);
                        fileChooseButton.setDisable(true);
                        dialogSettings.getItems().removeAll(dialogSettings.getItems());
                        dialogSettings.getItems().addAll(addFriend);
                        break;
                    case 3:
                        SendText.setText("Користувач видалив вас з друзів");
                        SendText.setEditable(false);
                        SendButton.setDisable(true);
                        fileChooseButton.setDisable(true);
                        dialogSettings.getItems().removeAll(dialogSettings.getItems());
                        dialogSettings.getItems().addAll(deleteFriend);
                        break;
                }
            } else {

                clearAllMessages();
                int selectedIndex = 1 + ListUsers.getSelectionModel().getSelectedIndex();
                LabelWho.setText(SearchName.get(selectedIndex) + " " + SearchLastName.get(selectedIndex));
                ViewPhoto.setImage(SearchImage.get(selectedIndex));
            }

        }
        );
    }

    @FXML
    public void addFriendButton(ActionEvent event) {

    }

    @FXML
    public void LoginKeyPress(KeyEvent event) {
        LoginText.setStyle("fx-border-color: none");
    }

    @FXML
    public void PassKeyPress(KeyEvent event) {
        PassText.setStyle("fx-border-color: none");
    }

    @FXML
    public void logoAction(MouseEvent event) {
        popMenu.show(logoButton, event.getScreenX(), event.getScreenY());
    }

    @FXML
    public void onRightHide(ActionEvent event) {
        Platform.runLater(() -> {
            Timeline timeline = new Timeline();
            if (Split.getRight() == null) {
                Split.setRight(RightVbox);
                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(RightVbox.prefWidthProperty(), 0)),
                        new KeyFrame(Duration.millis(350), new KeyValue(RightVbox.prefWidthProperty(), Rightprefwidth)));
                timeline.play();
            } else {
                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(RightVbox.prefWidthProperty(), Rightprefwidth)),
                        new KeyFrame(Duration.millis(350), new KeyValue(RightVbox.prefWidthProperty(), 0)));
                timeline.play();
                timeline.setOnFinished(event1 -> Split.setRight(null));
            }
        });
    }

    @FXML
    public void onLeftHide(ActionEvent event) {
        Platform.runLater(() -> {
            Timeline timeline = new Timeline();
            if (Split.getLeft() == null) {
                Split.setLeft(LeftVbox);
                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(LeftVbox.prefWidthProperty(), 0)),
                        new KeyFrame(Duration.millis(350), new KeyValue(LeftVbox.prefWidthProperty(), Leftprefwidth)));
                timeline.play();
            } else {
                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(LeftVbox.prefWidthProperty(), Leftprefwidth)),
                        new KeyFrame(Duration.millis(350), new KeyValue(LeftVbox.prefWidthProperty(), 0)));
                timeline.play();
                timeline.setOnFinished(event1 -> Split.setLeft(null));
            }
        });
    }

    @FXML
    public void ToolSettingButton(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                settingsListTriger = true;
                FXMLLoader loader = new FXMLLoader(javafxguiController.this.getClass().getResource("SettingsFXML.fxml"));
                loader.setController(javafxguiController.this);
                new SettingsStage(loader, Status.getScene().getWindow());
                PathTextField.setText(myPath);
                SettingsInfo.setEditable(true);
                ClientInfoPane.setVisible(true);
                ShowInfoPane.setVisible(false);
                SetingName.setText(MYName.get(1));
                SetingSurname.setText(MYLatName.get(1));
                SettingsImage.setImage(MYFoto.get(1));
                fillupComboBoxes();
                String[] userBirthday = MYBirthday.get(1).split("/");
                ComboDay.setValue(Integer.parseInt(userBirthday[0]));
                ComboMonth.setValue(Integer.parseInt(userBirthday[1]));
                ComboYear.setValue(Integer.parseInt(userBirthday[2]));
                ComboGender.setValue(MYGender.get(1));
                ComboCountry.setValue(MYCountry.get(1));
                SettingsInfo.setText(MYInformation.get(1));
            } catch (IOException e) {
                System.out.println("Settinds IO error" + Arrays.toString(e.getStackTrace())
                        + "\n" + e.getMessage());
            }
        });
    }

    private void fillupComboBoxes() {
        String[] lecales = Locale.getISOCountries();
        for (String cl : lecales) {
            Locale obj = new Locale("", cl);
            String[] city = {
                obj.getDisplayCountry()
            };
            for (int x = 0; x < city.length; x++) {
                countries.add(obj.getDisplayCountry());
            }
        }
        System.out.println("Items: " + countries.get(1));
        ComboCountry.setItems(countries);
        ComboGender.setItems(listGender);
        ComboYear.setItems(listYear);
        ComboMonth.setItems(listMonth);
        ComboDay.setItems(listDay);
    }

    @FXML
    public void AddFriendFromShow(ActionEvent event) {

    }

    @FXML
    public void changeSettingsImage(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                FileChooser choser = new FileChooser();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                FileChooser.ExtensionFilter extFilterBMP = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.BMP");
                choser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterBMP);
                imageFile = choser.showOpenDialog(SetingSurname.getScene().getWindow());
                BufferedImage buff = ImageIO.read(imageFile);
                Image imageF = SwingFXUtils.toFXImage(buff, null);
                SettingsImage.setImage(imageF);
            } catch (IOException e) {
                System.out.println("changeSettingsImage IOException");
            }
        });
    }

    @FXML
    public void saveSettingsChages(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                alert.setTitle("Добавлення друга");
                alert.setHeaderText("Ви добавлеєте користувача: " + LabelWho.getText());
                alert.setContentText("Ви хочите їх видалити ?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    if (imageFile != null) {
                        System.out.println("I send image");
                        netcon.sendString("COMDFOT",
                                imageFile.getAbsolutePath().substring(imageFile.getAbsolutePath().length() - 3)
                                + "EXTE" + Arrays.toString(java.nio.file.Files.readAllBytes(imageFile.toPath())));
                    }
                    netcon.sendString("COMDCNG", "UPDATE users SET "
                            + "  UserName = '" + SetingName.getText() + "' "
                            + ", UserLastName = '" + SetingSurname.getText() + "'"
                            + ", UserCountry = '" + ComboCountry.getValue() + "'"
                            + ", UserGender = '" + ComboGender.getValue() + "'"
                            + ", UserBirthday = '" + ComboDay.getValue() + "/" + ComboMonth.getValue() + "/" + ComboYear.getValue() + "'"
                            + ((SettingsInfo.getText() == null || SettingsInfo.getText().equals(""))
                            ? "" : ", UserInformation = '" + SettingsInfo.getText() + "'")
                            + "WHERE UserID = ");
                    netcon.sendString("MYIN", "");
                } else {
                    SetingName.setText(MYName.get(1));
                    SetingSurname.setText(MYLatName.get(1));
                    SettingsImage.setImage(MYFoto.get(1));
                    ComboCountry.setValue(MYCountry.get(1));
                    ComboGender.setValue(MYGender.get(1));
                    String[] userBirthday = MYBirthday.get(1).split("/");
                    ComboDay.setValue(Integer.parseInt(userBirthday[0]));
                    ComboMonth.setValue(Integer.parseInt(userBirthday[1]));
                    ComboYear.setValue(Integer.parseInt(userBirthday[2]));
                    alert.close();
                }

            } catch (IOException e) {
                System.out.println("saveSettingsChages IO");
            }
            ComboCountry.getScene().getWindow().hide();
        });
    }

    @FXML
    public void FriendsToolButton(ActionEvent event) {
        Platform.runLater(() -> {
            trigerList = true;
            clearAllUsers();
            netcon.sendString("INFOFRIENDS", "UserID");
            showTriger = false;
        });
    }

    @FXML
    public void onClientShowInfoImage(MouseEvent event) {
        ClientInfoShow();
    }

    @FXML
    public void LoginRegAction(ActionEvent event) {
        Platform.runLater(() -> {
            AnchorPaneRegist.setVisible(true);
            AnchorPaneLogin.setVisible(false);
            loginFrameClear();
            LoginText.setStyle("-fx-border-color:yellowgreen");
            PassText.setStyle("-fx-border-color:yellowgreen");
        });
    }

    @FXML
    public void RegLogButtonAction(ActionEvent event) {
        Platform.runLater(() -> {
            AnchorPaneRegist.setVisible(false);
            AnchorPaneLogin.setVisible(true);
            loginFrameClear();
            RegNameText.setStyle("-fx-border-color:yellowgreen");
            RegLogText.setStyle("-fx-border-color:yellowgreen");
            RegPassText.setStyle("-fx-border-color:yellowgreen");
        });
    }

    public void playNotificationSound() {
//        String filePath = myPath
//                + "NotificationSound" + "." + "mp3";
//        File AudioFile = new File(filePath);
//        if (!AudioFile.exists()) {
//            try (FileOutputStream fileOut = new FileOutputStream(AudioFile)) {
//                fileOut.write(MessageAudio.get(1 + MessageList.getSelectionModel().getSelectedIndex()));
//            } catch (Exception e) {
//                System.out.println("File send Exception ");
//            }
//        }
//        Media pick = new Media(filePath);
//        MediaPlayer player = new MediaPlayer(pick);
//        player.play();

    }
    
    @Override
    public void MsgRecive(NetConnect netconn, String msg) {
        playNotificationSound();
        switch (msg.substring(0, 4)) {
            case "FILE":
                FileRecive(msg.substring(4));
                break;
            case "PASS":
                changePasswordError.setText("Неправильний пароль");
                break;
            default:
                printMsg(msg);
        }
    }

    @Override
    public void onConnectReady(NetConnect netconn) {
    }
    
    @FXML
    public void changePathButton() {
        FileChooser choos = new FileChooser();
        File newDirectory = choos.showOpenDialog(OldPassword.getScene().getWindow());
        if(newDirectory.exists()) {
            myPath = newDirectory.getAbsolutePath();
            System.out.println("DIRECOTY CHANGED: "+myPath);
        }
    }
    @FXML
    public void ChangePasswordButton() {
        if (OldPassword.getText().equals("") || NewPassword.getText().equals("") || RepeateOldPassword.getText().equals("")) {
            OldPassword.setText("");
            NewPassword.setText("");
            RepeateOldPassword.setText("");
            changePasswordError.setText("Усі поля повинні бути заповнені");
        } else if (!NewPassword.getText().equals(RepeateOldPassword.getText())) {
            OldPassword.setText("");
            NewPassword.setText("");
            RepeateOldPassword.setText("");

            changePasswordError.setText("Паролі не спіпадають");
        } else {
            netcon.sendString("COMDCPS", OldPassword.getText().length() + "L" + OldPassword.getText() + "" + NewPassword.getText());
            OldPassword.setText("");
            NewPassword.setText("");
            RepeateOldPassword.setText("");

        }
    }
    
    @FXML
    public void onMyProfile(ActionEvent event) {
        AncorProfile.setVisible(true);
        AnchorOption.setVisible(false);
    }
    
    @FXML
    public void onOptionClick(ActionEvent event){
        AncorProfile.setVisible(false);
        AnchorOption.setVisible(true);
    }
    
    @Override
    public void onReceive(NetConnect netconn, String value) {
        Platform.runLater(() -> {
            switch (value) {
                case "true":
                    try {
                        netcon.sendString("", "ONLI");
                        LoginText.getScene().getWindow().hide();
                        FXMLLoader loader = new FXMLLoader(javafxguiController.this.getClass().getResource("javafxgui.fxml"));
                        loader.setController(javafxguiController.this);
                        new Javafxgui(loader);
                        Status.setStyle(""
                                + "-fx-text-fill: yellowgreen;\n"
                                + "-fx-font-weight: bold;\n"
                                + "-fx-font-size: 10pt;"
                        );
                        Status.setText("online");
                    } catch (Exception ex) {
                        System.out.println("Помилка створення основної форми");
                    }
                    break;
                case "false":
                    loginFrameClear();
                    LoginText.setStyle("-fx-border-color:red");
                    PassText.setStyle("-fx-border-color:red");
                    LabelHint.setStyle("-fx-text-fill: red");
                    LabelHint.setText("Неправильний логін або пароль");
                    break;
                case "Insertfase":
                    loginFrameClear();
                    RegLogText.setStyle("-fx-border-color:red");
                    LabelRegHint.setText("Користувач з такою почтою уже існує");
                    LabelRegHint.setStyle("-fx-text-fill: red");
                    break;
                case "Inserttrue":
                    loginFrameClear();
                    AnchorPaneLogin.setVisible(true);
                    AnchorPaneRegist.setVisible(false);
                    break;
                default:
                    System.out.println("Unknown msg: " + value);
            }
        });
    }

    public void loginFrameClear() {
        RegNameText.setText("");
        RegLogText.setText("");
        RegPassText.setText("");
        LoginText.setText("");
        PassText.setText("");
        RegLastName.setText("");
        LabelHint.setText("");
        RegHint.setText("");
    }

    @Override
    public void onDisconnect(NetConnect netconn) {
        Platform.runLater(() -> {
        });
    }

    @Override
    public void onException(NetConnect netconn, Exception e) {
        Platform.runLater(() -> {
        });
    }

    @Override
    public void InfoRecive(NetConnect netconn, String info) {

        int a = setUsersList(info.substring(4));
        System.out.println("C: " + a);
        if (a == 1) {
            System.out.println("A");
            createFriendsList();
        } else if (a == 2) {
            System.out.println("B");
            createSearchList();
        }

    }

    @Override
    public void ClientDiscon(NetConnect netconn) {
        Platform.runLater(() -> {
            if (isTrue == false) {
                Status.setStyle("-fx-text-fill: red");
                Status.setText("offline");
            }
        });
    }

    @Override
    public void ClientException(NetConnect netconn, Exception e) {

    }

    private void createFriendsList() {
        System.out.println("FriendsList");
        netcon.stopInfoThreads();
        Platform.runLater(() -> {
            for (Map.Entry<Integer, Integer> m : SearchID.entrySet()) {
                controlList.add(m.getKey());
            }
            ListUsers.setItems(controlList);
            ListUsers.setCellFactory(l -> new ListCell<Integer>() {
                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        try {
                            ImageView imageView = new ImageView();
                            Label labelName = new Label();
                            Label labelLastName = new Label();
                            Label labelMsgText = new Label();
                            HBox hboxMain = new HBox();
                            HBox hboxName = new HBox();
                            HBox setingBox = new HBox();
                            HBox hboxMsgText = new HBox();
                            VBox vbox = new VBox();
                            ImageView settings = new ImageView();
                            settings.setFitHeight(20);
                            settings.setFitWidth(40);
                            settings.setImage(bytesToImage(ToolsBag.get(5)));
                            Separator separ = new Separator();
                            imageView.setImage(SearchImage.get(item));
                            labelName.setId("labelSearchTitle");
                            labelLastName.setId("labelSearchTitle");
                            labelMsgText.setId("labelSearchText");
                            labelName.setText(SearchName.get(item));
                            labelLastName.setText(SearchLastName.get(item));
                            labelMsgText.setText((SearchLastMessage.get(item) != null) ? SearchLastMessage.get(item) : "empty");
                            imageView.setFitHeight(80);
                            imageView.setFitWidth(80);
                            setGraphic(hboxMain);
                            hboxMain.getChildren().add(imageView);
                            hboxMain.getChildren().add(vbox);
                            vbox.setStyle(
                                    (SearchStatus.get(item) == 0) ? 
                                            "fx-border-color: yellow" : "");
                            setingBox.setAlignment(Pos.CENTER_RIGHT);
                            setingBox.getChildren().add(hboxName);
                            vbox.getChildren().add(setingBox);
                            vbox.getChildren().add(hboxMsgText);
                            hboxName.getChildren().add(labelName);
                            hboxName.getChildren().add(labelLastName);
                            hboxName.getChildren().add(settings);
                            hboxMsgText.getChildren().add(labelMsgText);
                            vbox.getChildren().add(separ);
                            
                            if (!SearchMissingMessages.get(item).equals("0")) {
                                System.out.println("AAAAAAAAAA: "+SearchMissingMessages.get(item));
                                hboxMain.setEffect(shadowEffect);
                                shadowEffect.setWidth(30);
                                shadowEffect.setColor(Color.BLUE);
                            }
                            
                            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 -> {
                                ClientInfoShow();
                            });
                            
                            hboxMain.addEventHandler(MouseEvent.MOUSE_ENTERED, eventll -> {
                                hboxMain.setId("readed");
                            });
                            settings.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                dialogSettings.show(settings, event.getScreenX(), event.getScreenY());
                            });
                        } catch (IOException ex) {
                            System.out.println("friendsList IOException");
                        }
                    }
                }

            });
        });
    }

    private void createSearchList() {
        System.out.println("SearchList");
        netcon.stopInfoThreads();
        Platform.runLater(() -> {
            for (Map.Entry<Integer, Integer> m : SearchID.entrySet()) {
                controlList.add(m.getKey());
            }
            ListUsers.setItems(controlList);
            ListUsers.setCellFactory(l -> new ListCell<Integer>() {
                @Override
                public void updateItem(Integer item, boolean empty) {

                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        try {
                            ImageView imageView = new ImageView();
                            Label labelName = new Label();
                            Label labelLastName = new Label();
                            Label labelNameText = new Label();
                            Label labelLastNameText = new Label();
                            Label labelBirthday = new Label();
                            Label labelGender = new Label();
                            Label labelBirthdayText = new Label();
                            Label labelGenderText = new Label();
                            HBox hboxMain = new HBox();
                            HBox hboxName = new HBox();
                            HBox hboxLastName = new HBox();
                            HBox hboxBirthday = new HBox();
                            HBox hboxGender = new HBox();
                            VBox vbox = new VBox();
                            ImageView settings = new ImageView();
                            settings.setFitHeight(20);
                            settings.setFitWidth(40);
                            settings.setImage(bytesToImage(ToolsBag.get(2)));
                            imageView.setImage(SearchImage.get(item));
                            labelName.setId("labelSearchTitle");
                            labelNameText.setId("labelSearchText");
                            labelBirthday.setId("labelSearchTitle");
                            labelBirthdayText.setId("labelSearchText");
                            labelGender.setId("labelSearchTitle");
                            labelGenderText.setId("labelSearchText");
                            labelLastName.setId("labelSearchTitle");
                            labelLastNameText.setId("labelSearchText");
                            labelName.setText("Name: ");
                            labelLastName.setText("LastName: ");
                            labelGender.setText("Gender: ");
                            labelBirthday.setText("Birthday: ");
                            imageView.setFitHeight(80);
                            imageView.setFitWidth(80);
                            labelNameText.setText("" + SearchName.get(item));
                            labelLastNameText.setText("" + SearchLastName.get(item));
                            labelGenderText.setText("" + SearchGender.get(item));
                            labelBirthdayText.setText("" + SearchBirthday.get(item));
                            setGraphic(hboxMain);
                            hboxMain.getChildren().add(imageView);
                            hboxMain.getChildren().add(vbox);
                            vbox.getChildren().add(hboxName);
                            vbox.getChildren().add(hboxLastName);
                            vbox.getChildren().add(hboxBirthday);
                            vbox.getChildren().add(hboxGender);
                            hboxName.getChildren().add(labelName);
                            hboxName.getChildren().add(labelNameText);
                            hboxName.getChildren().add(settings);
                            hboxLastName.getChildren().add(labelLastName);
                            hboxLastName.getChildren().add(labelLastNameText);
                            hboxGender.getChildren().add(labelGender);
                            hboxGender.getChildren().add(labelGenderText);
                            hboxBirthday.getChildren().add(labelBirthday);
                            hboxBirthday.getChildren().add(labelBirthdayText);
                            
                            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 -> {
                                ClientInfoShow();
                            });

                            settings.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                netcon.sendString("COMD", "ADDID" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                            });
                        } catch (IOException ex) {
                            System.out.println("");
                        }
                    }
                }
            });
        });
    }

    private void createMessageList() {
        System.out.println("MessageList");
         netcon.stopMsListThreads();
            
        Platform.runLater(() -> {
            for (Map.Entry<Integer, Integer> m : MessageID.entrySet()) {
                messageList.add(m.getKey());
                if (MessageImage.get(m.getKey()) != null) {
                    System.out.println("Image(" + m.getKey() + ")" + MessageImage.get(m.getKey()).length);
                }
                if (MessageFile.get(m.getKey()) != null) {
                    System.out.println("File(" + m.getKey() + ")" + MessageFile.get(m.getKey()).length);
                }
                if (MessageAudio.get(m.getKey()) != null) {
                    System.out.println("Audio(" + m.getKey() + ")" + MessageAudio.get(m.getKey()).length);
                }
                if (MessageAudio.get(m.getKey()) != null) {
                    System.out.println("Message(" + m.getKey() + ")" + MessageText.get(m.getKey()));
                }
                
            }
            MessageList.setItems(messageList);
            MessageList.setContextMenu(deleteMessages);
            MessageList.setCellFactory(l -> new ListCell<Integer>() {
                @Override
                public void updateItem(Integer item, boolean empty) {

                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        try {
                            HBox mainBox = new HBox();
                            VBox messageBox = new VBox();
                            Label title = new Label();
                            Label messsage = new Label();
                            Label dateMsg = new Label();
                            ImageView media = new ImageView();
                            ImageView face = new ImageView();
                            HBox MessageInfo = new HBox();
                            face.setFitHeight(50);
                            face.setFitWidth(50);
                            mainBox.setId("vboxes");
                            messageBox.setId("vboxes");
                            title.setId("labelSearchTitle");
                            dateMsg.setId("labelSearchText");
                            messsage.setId("labelSearchText");
                            media.setFitHeight(200);
                            media.setFitWidth(200);
                            dateMsg.setText(MessageDate.get(item));
                            title.setText((MessageSort.get(item) == 0) ? MYName.get(1)
                                    : SearchName.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                            face.setImage((MessageSort.get(item) == 0) ? MYFoto.get(1)
                                    : SearchImage.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                            if (MessageSort.get(item) == 0) {
                                mainBox.getChildren().add(face);
                                mainBox.getChildren().add(messageBox);
                                messageBox.getChildren().add(MessageInfo);
                                MessageInfo.getChildren().addAll(title, dateMsg);
                            } else {
                                mainBox.getChildren().add(messageBox);
                                mainBox.getChildren().add(face);
                                messageBox.getChildren().add(MessageInfo);
                                MessageInfo.getChildren().addAll(dateMsg, title);
                            }
                            
                            if(MessageReaded.get(item).equals("1")) {
                                
                            } else {
                                messageBox.setEffect(shadowEffect);
                                shadowEffect.setWidth(30);
                                shadowEffect.setColor(Color.BLUE);
                            }
                            if (MessageText.get(item) != null) {
                                messsage.setText(MessageText.get(item));
                                messageBox.getChildren().add(messsage);
                            } else if (MessageFile.get(item) != null) {
                                media.setImage(bytesToImage(ToolsBag.get(3)));
                                messageBox.getChildren().add(media);
                            } else if (MessageAudio.get(item) != null) {
                                media.setImage(bytesToImage(ToolsBag.get(1)));
                                messageBox.getChildren().add(media);
                            } else if (MessageImage.get(item) != null) {
                                media.setImage(bytesToImage(MessageImage.get(item)));
                                messageBox.getChildren().add(media);
                            }

                            face.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 -> {
                                ClientInfoShow();
                            });

                            if (MessageSort.get(item) == 1) {
                                messageBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                                    System.out.println("In event");
                                    messageBox.setId("readed");
                                    netcon.sendString("COMDRMS", "ID" + SearchID.get(1 + ListUsers.getSelectionModel().getSelectedIndex()));
                                    if (MessageText.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) != null) {
                                        MessageSort.put(1 + ListUsers.getSelectionModel().getSelectedIndex(), 0);
                                    } else if (MessageFile.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) != null) {
                                        MessageSort.put(1 + ListUsers.getSelectionModel().getSelectedIndex(), 0);
                                    } else if (MessageAudio.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) != null) {
                                        MessageSort.put(1 + ListUsers.getSelectionModel().getSelectedIndex(), 0);
                                    } else if (MessageImage.get(1 + ListUsers.getSelectionModel().getSelectedIndex()) != null) {
                                        MessageSort.put(1 + ListUsers.getSelectionModel().getSelectedIndex(), 0);
                                    }
                                });
                            }
                            mainBox.setAlignment(MessageSort.get(item) == 0 ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
                            Platform.runLater(() -> {
                                setGraphic(mainBox);
                            });
                            messageBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    try {
                                        if (MessageFile.get(item) != null) {
                                            String filePath = myPath
                                                    + MYName.get(1) + MessageID.get(item) + "." + "txt";
                                            File file = new File(filePath);
                                            if (file.length() > 0) {
                                                Desktop.getDesktop().open(new File(filePath));
                                            } else {
                                                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                                                deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                                                file.createNewFile();
                                                FileOutputStream fout = new FileOutputStream(file);
                                                fout.write(MessageFile.get(item));
                                            }
                                        } else if (MessageImage.get(item) != null) {
                                            String filePath = myPath
                                                    + MYName.get(1) + MessageID.get(item) + "." + "png";
                                            File file = new File(filePath);
                                            if (file.length() > 0) {
                                                Desktop.getDesktop().open(new File(filePath));
                                            } else {
                                                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                                                deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                                                System.out.println("MessageImage: " + fileExtntion);
                                                InputStream in = new ByteArrayInputStream(MessageImage.get(item));
                                                BufferedImage image = ImageIO.read(in);
                                                ImageIO.write(image, "png", new File(filePath));
                                                System.out.println("File Loaded");
                                            }
                                        } else if (MessageAudio.get(item) != null) {
                                            String filePath = myPath
                                                    + MYName.get(1) + MessageID.get(item) + "." + "mp3";
                                            File AudioFile = new File(filePath);
                                            if (AudioFile.length() > 0) {
                                                Desktop.getDesktop().open(new File(filePath));
                                            } else {
                                                deleteMessages.getItems().removeAll(deleteMessages.getItems());
                                                deleteMessages.getItems().addAll(openFile, openInDir, deleteMessage, deleteMessageForme);
                                                try (FileOutputStream fileOut = new FileOutputStream(AudioFile)) {
                                                    fileOut.write(MessageAudio.get(item));
                                                } catch (Exception e) {
                                                    System.out.println("File send Exception ");
                                                }
                                            }
                                        }

                                    } catch (Exception b) {
                                        System.out.println("Exception Contetn");
                                    }
                                    event.consume();
                                }
                            });
                        } catch (IOException e) {
                            System.out.println("IMAGE IO EXCEPTION");
                        }
                    }
                }
            });
        });
    }

    public void FileRecive(String file) {
        try {
            switch (file.substring(0, 4)) {
                case "FILE":
                case "AUDI":
                case "IMAG":
                    if (addToMessages(file.substring(4), 1)) {
                        createMessageList();
                    }
            }
        } catch (IOException e) {
            System.out.println("FileRecive IOExeption");
        }
    }

    @Override
    public void MessageListRecive(NetConnect netcon, String message) {
        try {
            if (setMessageList(message)) {
                System.out.println("stopMsListThreds");
                System.out.println("CREATE MESSAGE LIST");
                createMessageList();
            }
        } catch (IOException e) {
            System.out.println("MessageListRecive IOExeption");
        }
    }

    @Override
    public void getMyInfo(NetConnect netconn, String info) {
        Platform.runLater(() -> {
            if (setMyInfoList(info)) {
                netcon.stopMyInfoThreads();
                MyFoto.setImage(MYFoto.get(1));
                LabelSecondName.setText(MYLatName.get(1));
                LabelName.setText(MYName.get(1));
                if (settingsListTriger == true) {
                    myPath = MYPath.get(1);
                    System.out.println("MY PATHH:: "+MYPath.get(1));
                    SetingName.setText(MYName.get(1));
                    SetingSurname.setText(MYLatName.get(1));
                    SettingsImage.setImage(MYFoto.get(1));
                    ComboCountry.setValue(MYCountry.get(1));
                    ComboGender.setValue(MYGender.get(1));
                    String[] userBirthday = MYBirthday.get(1).split("/");
                    ComboDay.setValue(Integer.parseInt(userBirthday[0]));
                    ComboMonth.setValue(Integer.parseInt(userBirthday[1]));
                    ComboYear.setValue(Integer.parseInt(userBirthday[2]));
                }
            }
        });
    }

    @Override
    public void ccResourcesRecive(NetConnect netconn, String info) {
        try {
            if (setTools(info)) {
                LogImg1.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg2.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg3.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg4.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg5.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg6.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg7.setImage(bytesToImage(ToolsBag.get(7)));
                LogImg8.setImage(bytesToImage(ToolsBag.get(7)));
                BorderImg1.setImage(bytesToImage(ToolsBag.get(6)));
                BorderImg2.setImage(bytesToImage(ToolsBag.get(6)));
                BorderImg3.setImage(bytesToImage(ToolsBag.get(6)));
                BorderImg4.setImage(bytesToImage(ToolsBag.get(6)));
                logoImage.setImage(bytesToImage(ToolsBag.get(9)));
                netconn.stopToolThreads();
            }
        } catch (IOException ex) {
            System.out.println("ccRR IOException");
        }
    }
}
