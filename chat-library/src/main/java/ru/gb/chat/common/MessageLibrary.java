package ru.gb.chat.common;

public class MessageLibrary {

    /*
     * /auth|request|login|password
     * /auth|accept|nickname
     * /auth|denied
     * /broadcast|msg
     *
     * /msg_format_error|msg
     * */

    public enum MESSAGE_TYPE {
        UNKNOWN,
        AUTH_ACCEPT,
        AUTH_DENIED,
        TYPE_BROADCAST,
        MSG_FORMAT_ERROR,
        TYPE_BROADCAST_CLIENT,
        USER_LIST
    }

    public static final String DELIMITER = "##";
    public static final String AUTH_METHOD = "/auth";
    public static final String AUTH_REQUEST = "request";
    public static final String AUTH_ACCEPT = "accept";
    public static final String AUTH_DENIED = "denied";

    /* то есть сообщение, которое будет посылаться всем */
    public static final String TYPE_BROADCAST = "/broadcast";

    /* если мы вдруг не поняли, что за сообщение и не смогли разобрать */
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";

    public static final String TYPE_BROADCAST_CLIENT = "/client_msg";
    public static final String USER_LIST = "/user_list";

    public static final String getAuthRequestMessage(String login, String password) {
        return AUTH_METHOD + DELIMITER + AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static final String getAuthAcceptMessage(String nickname) {
        return AUTH_METHOD + DELIMITER + AUTH_ACCEPT + DELIMITER + nickname;
    }

    public static String getAuthDeniedMessage() {
        return AUTH_METHOD + DELIMITER + AUTH_DENIED;
    }

    public static String getMsgFormatErrorMessage(String message) {
        return MSG_FORMAT_ERROR + DELIMITER + message;
    }

    public static String getBroadcastMessage(String src, String message) {
        return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() +
                DELIMITER + src + DELIMITER + message;
    }

    public static String getTypeBroadcastClient(String nickname, String msg) {
        return TYPE_BROADCAST_CLIENT + DELIMITER + nickname + DELIMITER + msg;
    }

    public static String getUserList(String users) {
        return USER_LIST + DELIMITER + users;
    }

    public static MESSAGE_TYPE getMessageType(String msg) {
        String[] arr = msg.split(DELIMITER);
        if (arr.length < 2) {
            return MESSAGE_TYPE.UNKNOWN;
        }
        String msgType = arr[0];
        switch (msgType) {
            case AUTH_METHOD:
                if (arr[1].equals(AUTH_ACCEPT)) {
                    return MESSAGE_TYPE.AUTH_ACCEPT;
                } else if (arr[1].equals(AUTH_DENIED)) {
                    return MESSAGE_TYPE.AUTH_DENIED;
                } else {
                    return MESSAGE_TYPE.UNKNOWN;
                }
            case TYPE_BROADCAST:
                return MESSAGE_TYPE.TYPE_BROADCAST;
            case TYPE_BROADCAST_CLIENT:
                return MESSAGE_TYPE.TYPE_BROADCAST_CLIENT;
            case MSG_FORMAT_ERROR:
                return MESSAGE_TYPE.MSG_FORMAT_ERROR;
            case USER_LIST:
                return MESSAGE_TYPE.USER_LIST;
            default:
                return MESSAGE_TYPE.UNKNOWN;
        }
    }
}
