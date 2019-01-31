package javafxgui;

import static javafxgui.NetConnect.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientInformation {
    protected final HashMap<Integer, Integer> SearchID = new HashMap<>();
    protected final HashMap<Integer, Image> SearchImage = new HashMap<>();
    protected final HashMap<Integer, String> SearchName = new HashMap<>();
    protected final HashMap<Integer, String> SearchBirthday = new HashMap<>();
    protected final HashMap<Integer, String> SearchGender = new HashMap<>();
    protected final HashMap<Integer, String> SearchLastName = new HashMap<>();
    protected final HashMap<Integer, String> SearchLastMessage = new HashMap<>();
    protected final HashMap<Integer, String> SearchInformation = new HashMap<>();
    protected final HashMap<Integer, Integer> MessageID = new HashMap<>();
    protected final HashMap<Integer, byte[]> MessageFile = new HashMap<>();
    protected final HashMap<Integer, byte[]> MessageImage = new HashMap<>();
    protected final HashMap<Integer, byte[]> MessageAudio = new HashMap<>();
    protected final HashMap<Integer, String> MessageText = new HashMap<>();
    protected final HashMap<Integer, String> MessageDate = new HashMap<>();
    protected final HashMap<Integer, Integer> MessageSort = new HashMap<>();
    protected final HashMap<Integer, String> MessageReaded = new HashMap<>();
    protected final HashMap<Integer, Integer> MYid = new HashMap<>();
    protected final HashMap<Integer, Image> MYFoto = new HashMap<>();
    protected final HashMap<Integer, String> MYName = new HashMap<>();
    protected final HashMap<Integer, String> MYLatName = new HashMap<>();
    protected final HashMap<Integer, String> MYBirthday = new HashMap<>();
    protected final HashMap<Integer, String> MYGender = new HashMap<>();
    protected final HashMap<Integer, String> MYCountry = new HashMap<>();
    protected final HashMap<Integer, String> MYInformation = new HashMap<>();
    protected final HashMap<Integer, String> MYPath = new HashMap<>();
    protected final HashMap<Integer, String> SearchCountry = new HashMap<>();
    protected final HashMap<Integer, Integer> SearchStatus = new HashMap<>();
    protected final HashMap<Integer, String> SearchMissingMessages = new HashMap<>();
    protected final HashMap<Integer, byte[]> ToolsBag = new HashMap<>();
    protected final ObservableList<Integer> controlList = FXCollections.observableArrayList();
    protected final ObservableList<Integer> messageList = FXCollections.observableArrayList();
    long time = new Date().getTime();
    private Integer UserID = 0, idForList = 0, nameForList = 0, imgForList = 0, genderForList = 0,
            birthdayForList = 0, lastnameForList = 0, msgForList = 0, countryForList = 0, idForMsg = 0, imgForMsg = 0, msgForMsg = 0,
            dateForMsg = 0, sortForMsg = 0, readedForMsg = 0, fileForMsg = 0, audioForMsg = 0, toolsImage = 0, statusForList = 0,
            informationForList = 0, misingForList = 0;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private int ResultSearchSize = 0, ResultMsgSize = 0, number = 0, f = 0, from = 0;
    protected String FileExtention = "";
    private final String FilePath = "C:\\Users\\Admin\\CharChatResources\\Pictures\\" + "CharChat" + time + "." + FileExtention + "";
    private String triger = "";
    private byte[] Media;
    private final int MAX_SIZE = 1000000;

    protected boolean addToMessages(String value, Integer sort) throws IOException {
        int place = 1 + messageList.size();
        int a134 = getExtention(value);
        System.out.println("From: " + a134);
        MessageDate.put(place, dateFormat.format(new Date()));
        MessageID.put(place, 9999);
        MessageReaded.put(place, "0");
        MessageSort.put(place, sort);

        System.out.println("FileExtention: " + FileExtention);
        switch (FileExtention.toLowerCase()) {
            case "png":
            case "jpg":
            case "bmp":
                System.out.println("Image: " + FileExtention);
                MessageImage.put(place, (convertStringToBytes(value.substring(a134))));
                MessageFile.put(place, null);
                MessageAudio.put(place, null);
                MessageText.put(place, null);
                break;
            case "mp3":
                System.out.println("MP3: " + FileExtention);
                MessageImage.put(place, null);
                MessageFile.put(place, null);
                MessageAudio.put(place, convertStringToBytes(value.substring(a134)));
                MessageText.put(place, null);
                break;
            case "pdf":
            case "txt":
                System.out.println("TXT: " + FileExtention);
                MessageImage.put(place, null);
                MessageFile.put(place, (convertStringToBytes(value.substring(a134))));
                MessageAudio.put(place, null);
                MessageText.put(place, null);
                break;
            case "msg":
                System.out.println("MSG: " + FileExtention);
                MessageImage.put(place, null);
                MessageFile.put(place, null);
                MessageAudio.put(place, null);
                MessageText.put(place, value.substring(a134));
                break;
        }
        if (messageList.size() > 0) {
            messageList.add(place);
            return false;
        } else {
            return true;
        }
    }

    protected boolean addToFriends(Integer id, String image, String name, String birthday,
                                   String gender, String lnm, String myinfo) throws IOException {
        int place = 1 + controlList.size();
        SearchID.put(place, id);
        SearchImage.put(place, bytesToImage(convertStringToBytes(image)));
        SearchName.put(place, name);
        SearchBirthday.put(place, birthday);
        SearchGender.put(place, gender);
        SearchLastName.put(place, lnm);
        SearchInformation.put(place, myinfo);
        if (controlList.size() > 0) {
            controlList.add(place);
            return false;
        } else {
            return true;
        }
    }

    protected boolean setMessageList(String message) throws IOException {
        triger = message.substring(0, 4);
        switch (triger) {
            case "SIZE":
                ResultMsgSize = Integer.parseInt(message.substring(4));
                System.out.println("RRRRRRRRRR: " + ResultMsgSize);
                break;
            case "MESG":
                int nuf = getNu(message);
                int puf = getNumber(message);
                String str;
                if (message.length() >= puf + 5) {
                    System.out.println("MESG: " + message.substring(puf, puf + 5));
                    str = (message.substring(puf, puf + 5).equalsIgnoreCase("empty")) ? null
                            : message.substring(puf);
                } else {
                    str = message.substring(puf);
                }
                System.out.println("MESSAGE NUMBER: " + number + " STR: " + str);
                MessageText.put(nuf, str);
                msgForMsg++;
                break;
            case "MSID":
                MessageID.put(getNu(message), Integer.parseInt(message.substring(getNumber(message))));
                idForMsg++;
                break;
            case "DATE":
                MessageDate.put(getNu(message), message.substring(getNumber(message)));
                dateForMsg++;
                break;
            case "SORT":
                MessageSort.put(getNu(message), Integer.parseInt(message.substring(getNumber(message))));
                sortForMsg++;
                break;
            case "READ":
                MessageReaded.put(getNu(message), message.substring(getNumber(message)));
                readedForMsg++;
                break;
            case "IMAG":
                int y = getNu(message);
                int hhh = getNumber(message);
                int from1 = getExtention(message.substring(hhh));
                Media = (message.substring(hhh + from1, hhh + from1 + 5).equalsIgnoreCase("empty")) ? null
                        : convertStringToBytes(message.substring(from1 + hhh));
                MessageImage.put(y, Media);
                imgForMsg++;
                break;
            case "FILE":
                int j1 = getNu(message);
                int j2 = getNumber(message);
                int from2 = getExtention(message.substring(j2));

                if (message.length() >= j2 + from2 + 5) {
                    Media = (message.substring(j2 + from2, j2 + from2 + 5).equalsIgnoreCase("empty")) ? null
                            : convertStringToBytes(message.substring(from2 + j2));
                } else {
                    Media = convertStringToBytes(message.substring(from2 + j2));
                }
                MessageFile.put(j1, Media);
                fileForMsg++;
                break;
            case "AUDI":

                int a1 = getNu(message);
                int a2 = getNumber(message);
                int from3 = getExtention(message.substring(a2));
                Media = (message.substring(a2 + from3, a2 + from3 + 5).equalsIgnoreCase("empty")) ? null
                        : convertStringToBytes(message.substring(from3 + a2));
                MessageAudio.put(a1, Media);
                audioForMsg++;
                break;
        }
        if (ResultMsgSize != 0) {
            if ((fileForMsg == ResultMsgSize) & (audioForMsg == ResultMsgSize) & (imgForMsg == ResultMsgSize)
                    & (msgForMsg == ResultMsgSize) & (idForMsg == ResultMsgSize) & (dateForMsg == ResultMsgSize)
                    & (sortForMsg == ResultMsgSize) & (readedForMsg == ResultMsgSize)) {
                for (int i = 0; i < mslistTreads.length; i++) {
                    System.out.println("Thread[" + i + "] isAlive(): " + mslistTreads[i].isAlive());
                }
                ResultMsgSize = 0;
                return true;
            }
        }
        return false;
    }

    protected int setUsersList(String info) {
        System.out.println("ResultSize: " + ResultSearchSize + "idForList: " + idForList + "nameForList: " + nameForList
                + "imgForList: " + imgForList + "GenderForList: " + genderForList + "birthdayForList" + birthdayForList
                + "msgForList: " + msgForList + "lastnameForList: " + lastnameForList + " ::::USCOO: " + countryForList + "status" + statusForList
                + " INFORMATION: " + informationForList);
        triger = info.substring(0, 4);
        switch (triger) {
            case "SIZE":
                ResultSearchSize = Integer.parseInt(info.substring(4));
                System.out.println("SIZE:" + ResultSearchSize);
                break;
            case "USID":
                SearchID.put(getNu(info), Integer.parseInt(info.substring(getNumber(info))));
                idForList++;
                break;
            case "USNM":
                SearchName.put(getNu(info), info.substring(getNumber(info)));
                nameForList++;
                break;
            case "USPH":
                try {
                    System.out.println("UserPhotoAded: " + imgForList);
                    SearchImage.put(getNu(info), bytesToImage(convertStringToBytes(info.substring(getNumber(info)))));
                    imgForList++;
                } catch (IOException e) {
                    System.out.println("USPH");
                }
                break;
            case "UGEN":
                SearchGender.put(getNu(info), info.substring(getNumber(info)));
                genderForList++;
                break;
            case "LSMS":
                SearchLastMessage.put(getNu(info), info.substring(getNumber(info)));
                msgForList++;
                break;
            case "USBI":
                SearchBirthday.put(getNu(info), info.substring(getNumber(info)));
                birthdayForList++;
                break;
            case "ULNM":
                int jok = getNumber(info);
                String ln = info.substring(jok).equalsIgnoreCase("empty") ? null : info.substring(jok);
                SearchLastName.put(getNu(info), ln);
                lastnameForList++;
                break;
            case "USCO":
                SearchCountry.put(getNu(info), info.substring(getNumber(info)));
                countryForList++;
                break;
            case "USFR":
                SearchStatus.put(getNu(info), Integer.parseInt(info.substring(getNumber(info))));
                statusForList++;
                break;
            case "USIN":
                SearchInformation.put(getNu(info), info.substring(getNumber(info)));
                informationForList++;
                break;
            case "USMI":
                SearchMissingMessages.put(getNu(info), info.substring(getNumber(info)));
                misingForList++;
                break;
        }
        if (ResultSearchSize > 0) {
            if ((idForList == ResultSearchSize) & (nameForList == ResultSearchSize) & (imgForList == ResultSearchSize)
                    & (birthdayForList == ResultSearchSize) & (genderForList == ResultSearchSize)
                    & (lastnameForList == ResultSearchSize) & (msgForList == ResultSearchSize) & (countryForList == ResultSearchSize)
                    & (statusForList == ResultSearchSize) & (informationForList == ResultSearchSize) & (misingForList == ResultSearchSize)) {
                ResultSearchSize = 0;
                return 1;
            } else if ((idForList == ResultSearchSize) & (nameForList == ResultSearchSize) & (imgForList == ResultSearchSize)
                    & (birthdayForList == ResultSearchSize) & (genderForList == ResultSearchSize)
                    & (lastnameForList == ResultSearchSize) & (msgForList == 0) & (countryForList == ResultSearchSize)
                    & (informationForList == ResultSearchSize)) {
                ResultSearchSize = 0;
                return 2;
            }
        }
        return 0;
    }

    protected boolean setMyInfoList(String info) {
        triger = info.substring(0, 4);
        String value = info.substring(4);
        switch (triger) {
            case "USID":
                MYid.put(1, Integer.parseInt(value));
                break;
            case "USNM":
                MYName.put(1, value);

                break;
            case "ULNM":
                MYLatName.put(1, value);

                break;
            case "UGEN":
                MYGender.put(1, value);

                break;
            case "USBI":
                MYBirthday.put(1, value);

                break;
            case "USPH":
                try {
                    MYFoto.put(1, bytesToImage(convertStringToBytes(value)));

                } catch (IOException e) {
                    System.out.println("USPH error");
                }
                break;
            case "USCO":
                MYCountry.put(1, value);
                break;
            case "USIN":
                MYInformation.put(1, value);
                break;
            case "USPT":
                MYPath.put(1, value);
                break;
        }

        return !MYid.isEmpty() & !MYBirthday.isEmpty() & !MYFoto.isEmpty()
                & !MYLatName.isEmpty() & !MYGender.isEmpty()
                & !MYName.isEmpty() & !MYCountry.isEmpty() & !MYInformation.isEmpty();
    }

    protected boolean setTools(String tools) {
        System.out.println("Add: " + number + ", count: " + threadTOOLLength);
        int N = getNu(tools);
        int s = getNumber(tools);
        ToolsBag.put(N, convertStringToBytes(tools.substring(s)));
        System.out.println("SIZE: " + ToolsBag.size());
        return (ToolsBag.size() == threadTOOLLength);
    }

    protected void clearAllMessages() {
        MessageID.clear();
        MessageFile.clear();
        MessageDate.clear();
        MessageReaded.clear();
        MessageSort.clear();
        MessageText.clear();
        messageList.clear();
        MessageAudio.clear();
        MessageImage.clear();
        ResultMsgSize = 0;
        idForMsg = 0;
        fileForMsg = 0;
        audioForMsg = 0;
        imgForMsg = 0;
        dateForMsg = 0;
        readedForMsg = 0;
        sortForMsg = 0;
        msgForMsg = 0;
    }

    protected void clearAllUsers() {
        SearchID.clear();
        SearchName.clear();
        SearchImage.clear();
        SearchLastMessage.clear();
        SearchBirthday.clear();
        SearchGender.clear();
        msgForList = 0;
        ResultSearchSize = 0;
        nameForList = 0;
        misingForList = 0;
        genderForList = 0;
        birthdayForList = 0;
        lastnameForList = 0;
        countryForList = 0;
        statusForList = 0;
        idForList = 0;
        imgForList = 0;
        informationForList = 0;
        controlList.clear();
    }

    private Integer getNu(String message) {
        int a = 5, b = 6, num;
        while (true) {
            if (message.substring(a, b).equals("N")) {
                num = Integer.parseInt(message.substring(4, a));
                break;
            }
            a++;
            b++;
        }
        return num;
    }

    private Integer getNumber(String ds) {
        int a1 = 5, b1 = 6;
        while (true) {
            if (ds.substring(a1, b1).equals("N")) {
                a1 = b1;
                break;
            }
        }
        return a1;
    }

    private byte[] convertStringToBytes(String convert) {
        byte[] allBytes = new byte[convert.length()];
        int abc = 1, whenStart = 0, change = 0, arrayLenght = 0, j = 1;
        String[] peregonBytov;
        byte[] byteFile1;

        while (true) {
            if ((MAX_SIZE * j) > convert.length()) {
                break;
            }
            j++;
        }
        while (abc <= j) {
            if (j < 2) {
                peregonBytov = convert.substring(1, convert.length() - 1).split(",");
                byteFile1 = new byte[peregonBytov.length];
                for (int i = 0; i < byteFile1.length; i++) {
                    byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                }
                System.arraycopy(byteFile1, 0, allBytes, 0, byteFile1.length);
                whenStart += byteFile1.length;
                break;
            } else {
                if (abc == 1) {
                    while (true) {
                        if (convert.substring(change + MAX_SIZE, change + MAX_SIZE + 1).equalsIgnoreCase(",")) {
                            peregonBytov = convert.substring(1, change + MAX_SIZE).split(",");
                            break;
                        }
                        change++;
                    }
                    byteFile1 = new byte[peregonBytov.length];
                    for (int i = 0; i < byteFile1.length; i++) {
                        byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                    }
                    System.arraycopy(byteFile1, 0, allBytes, whenStart, byteFile1.length);
                    arrayLenght = MAX_SIZE + change + 1;
                    whenStart += byteFile1.length;
                    abc++;
                } else if (abc == j) {
                    peregonBytov = convert.substring(arrayLenght, convert.length() - 1).split(",");
                    byteFile1 = new byte[peregonBytov.length];
                    for (int i = 0; i < byteFile1.length; i++) {
                        byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                    }
                    System.arraycopy(byteFile1, 0, allBytes, whenStart, byteFile1.length);
                    whenStart += byteFile1.length;
                    abc++;
                } else {
                    change = 0;
                    while (true) {
                        if (convert.substring(arrayLenght + MAX_SIZE + change, change + MAX_SIZE + arrayLenght + 1).equalsIgnoreCase(",")) {
                            peregonBytov = convert.substring(arrayLenght, change + arrayLenght + MAX_SIZE).split(",");
                            break;
                        }
                        change++;
                    }
                    byteFile1 = new byte[peregonBytov.length];
                    for (int i = 0; i < byteFile1.length; i++) {
                        byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                    }

                    System.arraycopy(byteFile1, 0, allBytes, whenStart, byteFile1.length);
                    whenStart += byteFile1.length;
                    arrayLenght = change + arrayLenght + MAX_SIZE + 1;
                    abc++;
                }
            }
        }

        byte[] bytesDestiny = new byte[whenStart];

        System.arraycopy(allBytes, 0, bytesDestiny, 0, whenStart);
        System.out.println("CCONNVVERRT ENDDDDD");
        return bytesDestiny;

    }

    protected Image bytesToImage(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        } else {
            InputStream in = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(in);
            Image imageF = SwingFXUtils.toFXImage(image, null);
            return imageF;
        }
    }

    private File bytesToFile(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        } else {
            File file = new File(FilePath);
            FileOutputStream fileout = new FileOutputStream(file);
            fileout.write(bytes);
            return file;
        }
    }

    private Integer getExtention(String file) {
        int z = 0, b = 4;
        while (true) {
            if (file.substring(z, b).equalsIgnoreCase("EXTE")) {
                FileExtention = file.substring(0, z);
                break;
            }
            z++;
            b++;
            System.out.println("While: " + file.substring(z, b));
        }
        return b;
    }

}
