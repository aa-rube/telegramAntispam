package app.bot.enviroment.messages;

import app.model.AdvertisersUser;
import org.springframework.stereotype.Service;

@Service
public class PartnersMessage {

    private final StringBuffer textMessage = new StringBuffer();

    public String userTimeEndMsgToUser(AdvertisersUser user) {
        textMessage.setLength(0);
        return textMessage.append("Время на публикацию рекламы в группе ")
                .append(user.getPermissionToGroup()).append(" закончилось.\n\nПо вопросам сотрудничества:")
                .append(user.getAdminUserNameOwner())
                .append("\n\n❗Если Ваше сообщение удалено, то скорее всего мы определи его как спам.").toString();
    }

    public String userTimeEndMsgToAdmin(AdvertisersUser user) {
        textMessage.setLength(0);
        return textMessage.append("У ").append(user.getUserName())
                .append(" закончился срок размещения рекламы в группе ").append(user.getPermissionToGroup()).toString();
    }

    public String postCountEndMsgToUser(AdvertisersUser user) {
        textMessage.setLength(0);
        return textMessage.append("Вы исчерпали все количество постов в группе ")
                .append(user.getPermissionToGroup()).append("\n")
                .append("По вопросам сотрудничества: ").append(user.getAdminUserNameOwner())
                .append("\n\n❗Если Ваше сообщение удалено, то скорее всего мы определи его как спам.").toString();
    }

    public String postCountEndMsgToAdmin(AdvertisersUser user) {
        textMessage.setLength(0);
        return textMessage.append("У ").append(user.getUserName()).append(" исчерпано количество постов для группы ")
                .append(user.getPermissionToGroup()).toString();
    }

    public String postCounterMsgToUser(AdvertisersUser user, int count) {
        textMessage.setLength(0);
        return textMessage.append("У вас осталось ").append(count).append(" постов до ")
                .append(user.getEndPermission().toString().substring(0, 16)).append("\n")
                .append("в группе ").append(user.getPermissionToGroup()).append("\n\n")
                .append("Если пост списался ошибочно, отправьте пожалуйста \"+\" менеджеру ")
                .append(user.getAdminUserNameOwner()).append(" чтобы вернуть списанный пост.").toString();
    }
}
