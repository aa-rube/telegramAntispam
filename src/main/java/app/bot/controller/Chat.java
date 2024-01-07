package app.bot.controller;

import app.bot.config.BotConfig;
import app.bot.enviroment.messages.*;
import app.handle.AdminsHandler;
import app.handle.StopWordHandle;
import app.handle.AdvertisersHandler;
import app.img.DownloadImg;
import app.img.IAmToken;
import app.model.*;
import app.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
public class Chat extends TelegramLongPollingBot {
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupsMessages groupsMessages;
    @Autowired
    private StartMenuMessage startMessage;
    @Autowired
    private StopWordMessage stopWordMessage;
    @Autowired
    private StopWordHandle stopWordHandler;
    @Autowired
    private AdvertisersMessage advertisersMessage;
    @Autowired
    private AdvertisersHandler advertisersHandler;
    @Autowired
    private StopWordService stopWordService;
    @Autowired
    private AdvertisersService advertisersService;
    @Autowired
    private AdminMessage adminMessage;
    @Autowired
    private AdminsHandler adminsHandler;
    @Autowired
    private BotAdminService botAdminService;
    @Autowired
    private DownloadImg downloadImg;
    @Autowired
    private IAmToken iAmToken;
    @Autowired
    private PartnersMessage partnersMsg;
    @Autowired
    private VipService vipService;
    @Autowired
    private SettingService settingService;

    private final Long adminChatId = -1001944648635L;//-1002038821841L;//
    private String yandexMainToken;
    private final HashMap<Long, Integer> chatIdMsgId = new HashMap<>();
    private final HashSet<Long> enterNewStopWord = new HashSet<>();
    private final HashMap<Long, AdvertisersUser> enterNewAdvertiser = new HashMap<>();
    private final HashSet<Long> enterNewAdmin = new HashSet<>();
    private final List<String> stopWords = new ArrayList<>();
    private final HashSet<String> advertisersUsersNames = new HashSet<>();
    private final HashSet<String> admins = new HashSet<>();
    private final HashMap<Long, List<Integer>> messageToDelete = new HashMap<>();
    private final HashMap<Long, Group> createNewGroup = new HashMap<>();
    private final HashSet<String> vipUsers = new HashSet<>();
    private final HashMap<String, String> groupMediaData = new HashMap<>();
    private final HashMap<Long, LocalDateTime> pretendedMute = new HashMap<>();
    private final HashMap<Long, Long> wereMuted = new HashMap<>();
    private final HashSet<Long> waitForNewVipUser = new HashSet<>();
    private final HashSet<Long> groupsChatId = GroupsList.getGroups();
    private final int aLongTime = (int) (System.currentTimeMillis() / 1000) + 10 * 365 * 24 * 60 * 60;
    private volatile Boolean checkPhoto = null;
    private volatile Integer lengthCaption = null;
    private volatile Integer count = null;
    private final HashSet<String> groupsBlackList = new HashSet<>();

    @Scheduled(fixedRate = 10800000)
    public void getIamToken() {
        yandexMainToken = iAmToken.getIAmToken(botConfig.getYandexToken(), yandexMainToken);
        groupMediaData.clear();
        saveTheSetting();
    }

    @Scheduled(fixedRate = 6000)
    public void saveDataTimer() {
        saveTheSetting();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void initializeSettings() {
        if (lengthCaption == null && checkPhoto == null && count == null) {
            if (settingService.getSetting().isPresent()) {
                Setting setting = settingService.getSetting().get();

                lengthCaption = setting.getCaptionLength();
                checkPhoto = setting.isCheckPhoto();
                count = setting.getCountPhoto();
            } else {
                lengthCaption = 10;
                checkPhoto = true;
                count = 0;
                saveTheSetting();
            }
        }
    }

    private void saveTheSetting() {
        settingService.save(lengthCaption, count, checkPhoto);
    }

    @PostConstruct
    public void init() {

        vipUsers.clear();
        stopWords.clear();
        advertisersUsersNames.clear();
        admins.clear();
        wereMuted.put(1L, 2L);

        initializeSettings();
        stopWords.addAll(stopWordService.getAllStopWords().stream()
                .map(StopWordObject::getWord)
                .toList());

        advertisersUsersNames.addAll(advertisersService.getAllUsers().stream()
                .map(AdvertisersUser::getUserName)
                .toList());

        admins.add(botConfig.getOwnerUserName());
        admins.add("@i_amallears");
        admins.addAll(botAdminService.getAllAdmins().stream()
                .map(BotAdmin::getName)
                .toList());

        for (VipUser user : vipService.findAllVipUsers()) {
            vipUsers.add(user.getUserName());
        }

        groupsBlackList.add("@Bangkok_sale");
        groupsBlackList.add("@phuketmotorent");
        groupsBlackList.add("@thai_good_work");
    }

    @Override
    public void onUpdateReceived(Update update) {

        try {
            if (update.hasMessage() && admins.contains("@" + update.getMessage().getFrom().getUserName())) {
                if (update.hasMessage()) {
                    textMessageHandle(update);
                }
            }

            if (update.hasCallbackQuery()) {
                if (update.hasCallbackQuery()
                        && admins.contains("@" + update.getCallbackQuery().getFrom().getUserName())) {
                    callBackDataHandle(update);
                }
            }

        } catch (Exception ignored) {
        }

        if (update.hasMessage()) {
            Message message = update.getMessage();
            String userName = "@" + message.getFrom().getUserName();

            if (groupsBlackList.contains(update.getMessage().getChat().getUserName())) return;

            if (vipUsers.contains(userName)) return;

            if (admins.contains(userName)) return;

            boolean isMediaGroup = update.getMessage().getMediaGroupId() != null;

            if (advertisersUsersNames.contains(userName)) {
                controlPartner(message, userName, isMediaGroup);
            } else {
                stopSpam(message, isMediaGroup);
            }
        }
    }

    public void controlPartner(Message message, String userName, boolean isMediaGroup) {
        String chatUserName = "@" + message.getChat().getUserName();
        AtomicBoolean moreThanZero = new AtomicBoolean(false);

        advertisersService.getAllUsersByUserName(userName).stream()
                .filter(user -> user.getPermissionToGroup().equals(chatUserName)
                        && user.getPostCount() > 0)
                .findFirst()
                .ifPresent(user -> {
                    moreThanZero.set(true);

                    if (!user.isStarted()) {
                        user.setStarted(true);
                        user.setEndPermission(LocalDateTime.now().plusDays(user.getDaysOfPermission()));
                    }

                    if (user.getEndPermission().isBefore(LocalDateTime.now())) {
                        stopSpam(message, isMediaGroup);

                        executeWithoutDelete(message.getFrom().getId(), partnersMsg.userTimeEndMsgToUser(user));
                        executeWithoutDelete(user.getAdminChatIdOwner(), partnersMsg.userTimeEndMsgToAdmin(user));
                        return;
                    }

                    if (messageIsNotGroupMediaMessage(message, userName)) {
                        int postCount = user.getPostCount() - 1;
                        user.setPostCount(postCount);
                        advertisersService.deleteByUserId(user.getId());
                        user.setId(null);
                        advertisersService.save(user);
                        executeWithoutDelete(message.getFrom().getId(), partnersMsg.postCounterMsgToUser(user, postCount));
                        return;
                    }
                    stopSpam(message, isMediaGroup);
                });

        if (messageIsNotGroupMediaMessage(message, userName) && !moreThanZero.get()) {

            advertisersService.getAllUsersByUserName(userName).stream()
                    .filter(user -> user.getPermissionToGroup().equals(chatUserName)
                            && user.getPostCount() == 0)
                    .findFirst()
                    .ifPresent(user -> {
                        stopSpam(message, isMediaGroup);
                        executeWithoutDelete(message.getFrom().getId(), partnersMsg.postCountEndMsgToUser(user));
                        executeWithoutDelete(user.getAdminChatIdOwner(), partnersMsg.postCountEndMsgToAdmin(user));
                    });
        }
    }


    private boolean messageIsNotGroupMediaMessage(Message message, String userName) {
        if (message.getMediaGroupId() != null && !groupMediaData.containsKey(message.getMediaGroupId())
                || message.getMediaGroupId() == null) {

            if (message.getMediaGroupId() != null) {
                groupMediaData.put(message.getMediaGroupId(), userName);
            }
            return true;
        }
        return false;
    }

    private void stopSpam(Message message, boolean isMediaGroup) {
        if (message.hasText() && !message.hasPhoto()) {
            deleteMessageIfTheTextIsBad(message, message.getText().toLowerCase());
            return;
        }

        try {
            if (message.hasPhoto() && message.getCaption() != null) {
                deleteMessageIfTheTextIsBad(message, message.getCaption().toLowerCase());
                deleteMessageIfThePhotoIsBad(message, isMediaGroup);
                return;
            }

            if (message.hasPhoto() && message.getCaption() == null) {

                deleteMessageIfThePhotoIsBad(message, isMediaGroup);
                return;
            }

            if (message.hasAnimation() || message.hasAudio() || message.hasDice() || message.hasDocument() ||
                    message.hasContact() || message.hasInvoice() || message.hasLocation() || message.hasSticker() ||
                    message.hasVideo() || message.hasVideoNote() || message.hasVoice() || message.hasAudio() ||
                    message.hasPoll() || message.hasPassportData() && message.getCaption() != null) {

                deleteMessageIfTheTextIsBad(message, message.getCaption().toLowerCase());
            }
        } catch (Exception e) {
        }
    }

    private void deleteMessageIfTheTextIsBad(Message message, String content) {
        stopWords.stream()
                .filter(stopWord -> content.toLowerCase().contains(stopWord.toLowerCase().trim()))
                .forEach(stopWord -> {
                    mute(message);
                    deleteBadMessage(message.getChatId(), message.getMessageId());
                    sendThisMessage(message, stopWord);
                });
    }

    private void deleteMessageIfThePhotoIsBad(Message message, boolean isMediaGroup) throws Exception {
        if (!checkPhoto || (message.getCaption() != null && message.getCaption().length() > lengthCaption) || isMediaGroup) {
            return;
        }

        count++;
        String filePhotoId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
        GetFile getFile = new GetFile();
        getFile.setFileId(filePhotoId);
        File file = execute(getFile);

        deleteMessageIfTheTextIsBad(message,
                downloadImg.getPhotoText(file, filePhotoId, botConfig.getToken(),
                        yandexMainToken, botConfig.getFolderYandexId()).toLowerCase());
    }

    private void mute(Message message) {
        Long userId = message.getFrom().getId();
        Long groupId = message.getChatId();
        String userName = message.getFrom().getUserName();
        groupsChatId.add(groupId);

        if (pretendedMute.containsKey(userId)) {

            if (wereMuted.get(userId) != null && wereMuted.get(userId).equals(groupId)) {
                restrictUser(groupId, userId, aLongTime);
                executeWithoutDelete(adminChatId,
                        "Пользователь " + userName + ", " + message.getFrom().getId() +
                                " замьючен на всегда в группе " + message.getChat().getUserName());
                wereMuted.remove(userId);
                return;
            }


            LocalDateTime lastOperationTime = pretendedMute.get(userId);
            LocalDateTime currentTime = LocalDateTime.now();
            long minutesElapsed = ChronoUnit.MINUTES.between(lastOperationTime, currentTime);

            if (minutesElapsed < 1) {
                int time = (int) (System.currentTimeMillis() / 1000) + 10800;
                wereMuted.put(userId, groupId);

                executeWithoutDelete(adminChatId,
                        "Пользователь " + userName + ", " + userId
                                + " замьючен на 3 часа во всех группах");

                for (Long chatGroup : groupsChatId) {
                    Thread thread = new Thread(() -> {
                        restrictUser(chatGroup, userId, time);
                    });
                    thread.start();
                }

                return;
            }
        }
        pretendedMute.put(userId, LocalDateTime.now());
    }

    private void restrictUser(Long chatId, Long userId, int time) {
        RestrictChatMember member = new RestrictChatMember();
        member.setChatId(chatId);
        member.setUserId(userId);

        ChatPermissions permissions = new ChatPermissions();
        permissions.setCanSendMessages(false);
        member.setPermissions(permissions);
        member.setUntilDate(time);

        try {
            executeAsync(member);
        } catch (TelegramApiException e) {
        }
    }

    private void sendThisMessage(Message message, String badWord) {

        try {
            String group = "@" + message.getChat().getUserName();
            String userName = "@" + message.getFrom().getUserName();

            if (!message.hasPhoto()) {
                SendMessage msg = new SendMessage();
                msg.setChatId(adminChatId);
                msg.setText(userName + "\n" + group + "\n" + message.getText() + "\n\nПричина: " + badWord);
                msg.setEntities(message.getEntities());
                executeAsync(msg);
                return;
            }

            if (message.hasPhoto()) {
                SendPhoto msg = new SendPhoto();
                msg.setChatId(adminChatId);
                msg.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
                msg.setCaption(userName + "\n" + group + "\n" + message.getCaption() + "\n\nПричина: " + badWord);
                msg.setCaptionEntities(message.getCaptionEntities());
                executeAsync(msg);
            }

        } catch (Exception e) {
            SendMessage msg = new SendMessage();
            msg.setChatId(adminChatId);
            msg.setText("@" + message.getFrom().getUserName() + "\nВложение не удалось загрузить.\nТекст:" +
                    message.getCaption() + "\n\nПричина: " + badWord);
            try {
                executeAsync(msg);
            } catch (TelegramApiException ex) {
            }
        }
    }

    private void callBackDataHandle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();

        deleteMessage(chatId);
        clearWaitList(chatId);

        mainMenuHandler(update, chatId, data);
        stopWordHandler(chatId, data);
        advertisersHandler(update, chatId, data);
        adminHandler(chatId, data);
        vipUsersHandler(chatId, data);
    }

    private final HashSet<Long> enterNewLength = new HashSet<>();

    private void mainMenuHandler(Update update, Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 0) {

                executeMsg(startMessage.getStopWordMainMenu(chatId));
                return;
            }

            if (i == 1) {
                executeMsg(startMessage.getAdvertisersMainOption(chatId));
                return;
            }

            if (i == 3) {
                executeMsg(startMessage.getAdminsMainMenu(chatId));
                return;
            }

            if (i == 33) {
                if (checkPhoto) {
                    checkPhoto = false;
                } else {
                    checkPhoto = true;
                }
                saveTheSetting();
                executeMsg(adminMessage.getSettingsMsg(chatId, count, lengthCaption, checkPhoto));
            }

        } catch (Exception e) {

            if (data.equals("setCount")) {
                enterNewLength.add(chatId);
                executeMsg(adminMessage.enterNewLength(chatId));
            }

            if (data.equals("settings")) {
                enterNewLength.clear();
                executeMsg(adminMessage.getSettingsMsg(chatId, count, lengthCaption, checkPhoto));
            }

            if (data.equals("backToStart")) {

                if (botConfig.getOwnerUserName().equals("@" + update.getCallbackQuery().getFrom().getUserName()) ||
                        "@i_amallears".equals("@" + update.getCallbackQuery().getFrom().getUserName())) {

                    executeMsg(startMessage.getMainOwnerMenu(chatId, checkPhoto));
                    return;
                }
                executeMsg(startMessage.getMainAdminMenu(chatId));
            }

            if (data.contains("getGroups_")) {
                int index = Integer.parseInt(data.split("_")[1]);
                executeMsg(advertisersMessage.getGroupsKeyList(chatId,
                        enterNewAdvertiser.get(chatId).getUserName(), index));
            }

            if (data.contains("setG:")) {
                String group = data.split(":")[1];
                enterNewAdvertiser.get(chatId).setPermissionToGroup(group);
                executeMsg(advertisersMessage.addCountPostKeys(chatId, enterNewAdvertiser.get(chatId)));
            }

            if (data.contains("dayCount_")) {
                int count = Integer.parseInt(data.split("_")[1]);
                int days = Integer.parseInt(data.split("_")[2]);

                enterNewAdvertiser.get(chatId).setDaysOfPermission(days);
                enterNewAdvertiser.get(chatId).setEndPermission(null);
                enterNewAdvertiser.get(chatId).setPostCount(count);

                if (advertisersService.save(enterNewAdvertiser.get(chatId))) {
                    executeMsg(advertisersMessage.dataSaved(chatId, enterNewAdvertiser.get(chatId)));
                    enterNewAdvertiser.put(chatId, advertisersService.getNewObjectCallBackQuery(update, chatId));
                    init();
                } else {
                    executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
                    executeMsg(advertisersMessage.addCountPostKeys(chatId, enterNewAdvertiser.get(chatId)));
                }
            }
        }
    }

    private void sendListStopWords(Long chatId) {
        List<StopWordObject> s = stopWordService.getAllStopWords();
        s.sort(Comparator.comparing(StopWordObject::getWord));

        int batchSize = 60;
        for (int startIndex = 0; startIndex < s.size(); startIndex += batchSize) {
            int endIndex = Math.min(startIndex + batchSize, s.size());
            List<StopWordObject> batch = s.subList(startIndex, endIndex);

            executeMsg(stopWordMessage.getListWords(chatId, batch));
        }
    }

    private void stopWordHandler(Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 4) {
                sendListStopWords(chatId);
                return;
            }


            if (i == 5) {
                enterNewStopWord.add(chatId);
                executeMsg(stopWordMessage.enterNewStopWord(chatId));
                init();
            }
        } catch (Exception e) {
        }
    }

    private void adminHandler(Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 8) {
                executeMsg(adminMessage.getAdminsList(chatId));
            }

            if (i == 9) {
                enterNewAdmin.add(chatId);
                executeMsg(adminMessage.getAddNewAdmin(chatId));
            }
        } catch (Exception e) {

        }
    }

    private void advertisersHandler(Update update, Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 7) {
                enterNewAdvertiser.put(chatId, advertisersHandler.getNewObject(update, chatId));
                executeMsg(advertisersMessage.addAdvertiser(chatId));
                return;
            }

            if (i == 11) {
                createNewGroup.put(chatId, groupService.getNewObjectByCallbackUpdate(update));
                executeMsg(groupsMessages.getEnterNewGroupUserName(chatId));
            }

            if (i == 12) {
                executeMsg(groupsMessages.getListGroup(update.getCallbackQuery().getFrom().getUserName(), chatId));
            }

            if (i == 13) {
                enterNewAdvertiser.remove(chatId);
                executeMsg(advertisersMessage.getAdvertisersStart(chatId));
            }

            if (i == 14) {
                enterNewAdvertiser.put(chatId, advertisersService.getNewObjectCallBackQuery(update, chatId));
                executeMsg(advertisersMessage.addAdvertiser(chatId));
            }

            if (i == 15) {
                executeMsg(advertisersMessage.getListUsersPermission(chatId));
            }

        } catch (Exception e) {

        }
    }

    private void vipUsersHandler(Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);
            if (i == 88) {
                waitForNewVipUser.remove(chatId);
                executeMsg(adminMessage.vipUsersMainMenu(chatId));
                return;
            }

            if (i == 87) {
                waitForNewVipUser.add(chatId);
                executeMsg(adminMessage.addVipUsers("", chatId));
            }
            if (i == 86) {
                waitForNewVipUser.remove(chatId);
                executeMsg(adminMessage.getVipListUser(chatId));
            }

        } catch (Exception e) {

        }
    }

    private void textMessageHandle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.equals("/start")) {
            init();
            clearWaitList(chatId);

            if (botConfig.getOwnerUserName().equals("@" + update.getMessage().getFrom().getUserName())
                    || "@i_amallears".equals("@" + update.getMessage().getFrom().getUserName())) {

                executeMsg(startMessage.getMainOwnerMenu(chatId, checkPhoto));
                return;
            }
            try {
                executeMsg(startMessage.getMainAdminMenu(chatId));
                return;
            } catch (Exception e) {

            }
        }


        if (text.contains("/deleteStopWord_")) {
            deleteMessage(chatId);

            if (stopWordHandler.deleteTheWord(text)) {
                executeMsg(stopWordMessage.deletedSuccess(chatId));
                init();

            } else {
                executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
                sendListStopWords(chatId);
            }
            return;
        }


        if (text.contains("/deleteAdmin_")) {
            deleteMessage(chatId);
            if (adminsHandler.deleteTheAdmin(text)) {
                executeMsg(adminMessage.getAdminsList(chatId));
                init();
            } else {
                executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
                executeMsg(adminMessage.getAdminsList(chatId));
            }
            return;
        }

        if (text.contains("/deleteGroup_")) {
            String userName = update.getMessage().getFrom().getUserName();
            if (groupService.deleteById(text.trim())) {
                executeMsg(groupsMessages.getListGroup(userName, chatId));
            } else {
                executeMsg(groupsMessages.getListGroup(userName, chatId));
                executeMsg(startMessage.getSendMessage(chatId,
                        "Что-то пошло не так! Попробуйте снова!", null));
            }
            return;
        }

        if (text.contains("/deletePartner_")) {
            try {
                int id = Integer.parseInt(text.trim().split("_")[1]);

                if (advertisersService.deleteByUserId(id)) {
                    executeMsg(advertisersMessage.getListUsersPermission(chatId));
                } else {
                    executeMsg(advertisersMessage.getListUsersPermission(chatId));
                    executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
                }
                init();
                return;
            } catch (Exception e) {

            }
        }

        if (text.contains("/addOnePost_")) {
            editAdvertisersPostsCount(chatId, text, 1);
        }

        if (text.contains("/removeOnePost_")) {
            editAdvertisersPostsCount(chatId, text, (-1));
        }

        if (text.contains("/deleteVip@")
                && (("@" + update.getMessage().getFrom().getUserName()).equals(botConfig.getOwnerUserName())
                || admins.contains("@" + update.getMessage().getFrom().getUserName()))) {

            String userName = "@" + text.split("@")[1];

            if (vipService.delete(userName)) {
                vipUsers.remove(userName);
                executeWithoutDelete(chatId, userName + " удален из вип списка");
            } else {
                executeWithoutDelete(chatId, "Не смог найти " + userName);
            }
            return;
        }
        if (enterNewLength.contains(chatId)) {
            try {
                lengthCaption = Integer.parseInt(text);
                saveTheSetting();
                executeMsg(adminMessage.getSettingsMsg(chatId, count, lengthCaption, checkPhoto));
            } catch (Exception e) {
                executeMsg(startMessage.getSendMessage(chatId, "Что-то пошло не так, повторите снова.", null));
            }
        }

        if (waitForNewVipUser.contains(chatId)) {
            if (text.split("\\s+").length >= 2) {

                for (String s : text.split("\\s+")) {
                    if (s.trim().replace(" ", "").contains("@")) {
                        vipService.save(s.trim().replace(" ", ""));
                        vipUsers.add(s.trim().replace(" ", ""));
                    }
                }
                executeMsg(adminMessage.addVipUsers("Данные сохранены!\n", chatId));
                return;
            }

            if (text.contains("@")) {
                vipService.save(text.trim().replace(" ", ""));
                vipUsers.add(text.trim().replace(" ", ""));
                executeMsg(adminMessage.addVipUsers("Данные сохранены!\n", chatId));
                return;
            }

            executeMsg(adminMessage.addVipUsers("Что-то пошло не так!\n", chatId));
            return;
        }


        if (enterNewStopWord.contains(chatId)) {

            if (stopWordHandler.saveTheWord(text)) {
                executeWithoutDelete(chatId, "Cохраненo! Можно добавить еще.");
                executeMsg(stopWordMessage.enterNewStopWord(chatId));
                init();
            } else {
                executeMsg(startMessage.getSendMessage(chatId,
                        text + "Что-то пошло не так! Попробуйте снова!", null));
                executeMsg(stopWordMessage.enterNewStopWord(chatId));
            }

            return;
        }

        if (enterNewAdmin.contains(chatId)) {
            if (!text.trim().isEmpty() && adminsHandler.saveTheAdmin(text.trim())) {
                executeWithoutDelete(chatId, text.trim() + " сохранено.");
                executeMsg(adminMessage.getAddNewAdmin(chatId));
                init();
            } else {
                executeMsg(startMessage.getSendMessage(chatId,
                        text + "Что-то пошло не так! Попробуйте снова!", null));
            }
            return;
        }

        try {
            if (createNewGroup.get(chatId).getOwnerChatId().equals(chatId) &&
                    text.contains("@") && groupService.save(createNewGroup.get(chatId), text.trim())) {

                executeWithoutDelete(chatId, "Cохраненo! Можно добавить еще.");
                createNewGroup.put(chatId, groupService.getNewObjectByTextUpdate(update));
                executeMsg(groupsMessages.getEnterNewGroupUserName(chatId));
            } else {
                executeMsg(startMessage.getSendMessage(chatId,
                        "Что-то пошло не так! Попробуйте снова!", null));
            }


        } catch (Exception e) {
            try {
                if (text.contains("@") && enterNewAdvertiser.get(chatId).getAdminChatIdOwner().equals(chatId)) {
                    enterNewAdvertiser.get(chatId).setUserName(text.trim());
                    executeMsg(advertisersMessage.getGroupsKeyList(chatId, text.trim(), 0));
                }
            } catch (Exception inored) {
                executeMsg(startMessage.getSendMessage(chatId,
                        "Что-то пошло не так! Попробуйте снова!", null));
            }
        }
    }

    private void editAdvertisersPostsCount(Long chatId, String text, int count) {
        int id = Integer.parseInt(text.split("_")[1]);
        AdvertisersUser user = advertisersService.getUserById(id);
        advertisersService.deleteByUserId(id);
        user.setId(null);
        user.setPostCount(user.getPostCount() + count);
        advertisersService.save(user);

        executeMsg(advertisersMessage.getListUsersPermission(chatId));
        init();
    }

    private void executeWithoutDelete(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
        }
    }

    private void executeMsg(SendMessage msg) {
        if (!messageToDelete.isEmpty()) {
            Long chatId = Long.valueOf(msg.getChatId());
            if (messageToDelete.containsKey(chatId)) {
                for (Integer i : messageToDelete.get(chatId)) {
                    DeleteMessage d = new DeleteMessage();
                    d.setChatId(chatId);
                    d.setMessageId(i);

                    try {
                        execute(d);
                    } catch (TelegramApiException e) {
                    }
                }
                messageToDelete.remove(chatId);
            }
        }

        String text = msg.getText();
        int chunkSize = 4000;

        if (text.length() > chunkSize) {
            int numChunks = (int) Math.ceil((double) text.length() / chunkSize);

            for (int i = 0; i < numChunks; i++) {
                int start = i * chunkSize;
                int end = Math.min((i + 1) * chunkSize, text.length());
                String chunk = text.substring(start, end);

                SendMessage chunkMsg = new SendMessage();
                chunkMsg.setChatId(msg.getChatId());
                chunkMsg.setText(chunk);
                chunkMsg.setReplyMarkup(msg.getReplyMarkup());

                try {
                    int msgId = execute(chunkMsg).getMessageId();
                    Long chatIdChunkMsg = Long.valueOf(chunkMsg.getChatId());

                    if (messageToDelete.containsKey(chatIdChunkMsg)) {
                        messageToDelete.get(chatIdChunkMsg).add(msgId);
                    } else {
                        List<Integer> list = new ArrayList<>();
                        list.add(msgId);
                        messageToDelete.put(chatIdChunkMsg, list);
                    }
                } catch (TelegramApiException e) {

                }
            }

        } else {
            try {
                chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
            } catch (TelegramApiException e) {

            }
        }
    }

    public void deleteMessage(Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(chatIdMsgId.get(chatId));
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {

        }

    }

    private void deleteBadMessage(Long chatId, int messageId) {
        DeleteMessage delete = new DeleteMessage();
        delete.setChatId(chatId);
        delete.setMessageId(messageId);
        try {
            execute(delete);
        } catch (TelegramApiException e) {
        }
    }

    private void clearWaitList(Long chatId) {
        waitForNewVipUser.remove(chatId);
        enterNewStopWord.remove(chatId);
        enterNewAdmin.remove(chatId);
        createNewGroup.remove(chatId);
    }
}