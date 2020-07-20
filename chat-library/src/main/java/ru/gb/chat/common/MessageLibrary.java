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


    public static final String DELIMITER = "##";
    public static final String AUTH_METHOD = "/auth";
    public static final String AUTH_REQUEST = "request";
    public static final String AUTH_ACCEPT = "accept";
    public static final String AUTH_DENIED = "denied";

    /* если мы вдруг не поняли, что за сообщение и не смогли разобрать */
    public static final String TYPE_BROADCAST = "/broadcast";

    /* то есть сообщение, которое будет посылаться всем */
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";

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
}
