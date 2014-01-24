package com.platforms.utils;

/**
 * Created by AGalkin on 9/26/13.
 */
public class Endpoints {
    public static String XMPPhost = "beta.cancerlife.net";
    public static String XMPPport = "5222";
    public static String main = "https://beta.cancerlife.net/";
    public static String login = main+"rest/login";
    public static String logout = main+"rest/logout";
    public static String register = main+"rest/register";
    public static String registerFields = main+"rest/get_register_fields/";
    public static String journal = main+"rest/view_journal/";
    public static String postProfile = main+"rest/profile/";
    public static String getProfile = main+"rest/view_profile/";
    public static String getPhoto = main+"rest/view_photo/";
    public static String postPhoto = main+"rest/photo_base64/";
    public static String getSupportersList = main+"rest/view_supporters/";
    public static String inviteSupporter = main+"rest/new_supporters/";
    public static String comments = main + "rest/get_journal_comments/";
    public static String journalFields = main + "rest/get_journal_fields/";
    public static String newComment = main + "rest/new_journal_comment";
    public static String newJournal = main + "rest/journal/";
    public static String patients = main + "rest/patients/";
    public static String reports = main + "rest/reports";
    public static String getChats = main + "rest/get_chats";
    public static String getMessages = main + "rest/get_messages/";
}
