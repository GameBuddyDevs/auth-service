package com.back2261.authservice.util;

public class Constants {
    public static final String EMAIL_SUBJECT = "Gamebuddy - Your email verification code: %s";
    public static final String EMAIL_TEXT =
            """
            Thank you for registering on Gamebuddy!
            Here is your verification code: %s
            Have fun!
            """;
    public static final String EMAIL_SUBJECT_FORGOT_PASSWORD = "Gamebuddy – Password Reset";
    public static final String EMAIL_TEXT_FORGOT_PASSWORD =
            """
            Someone requested that the password be reset for this account.
            Here is your verification code to continue: %s
            Your email: %s
            If you did not request a password reset, please ignore this email.
            """;

    private Constants() {}
}
