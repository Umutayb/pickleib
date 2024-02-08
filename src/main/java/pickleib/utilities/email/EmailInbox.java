package pickleib.utilities.email;

import collections.Pair;
import context.ContextStore;
import lombok.*;
import utils.EmailUtilities;
import utils.Printer;
import utils.reflection.ReflectionUtilities;

import static utils.EmailUtilities.Inbox.EmailField.CONTENT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class EmailInbox {

    Printer log = new Printer(EmailInbox.class);
    EmailUtilities.Inbox.EmailField filter;
    String filterKey;
    String initialKeyword;
    String finalKeyword;
    Boolean print;
    Boolean save;
    Boolean saveAttachments;
    String host;
    String port;
    String secureCon;
    String email;
    String emailApplicationPassword;
    public long emailAcquisitionTimeout = Long.parseLong(ContextStore.get("email-acquisition-timeout", "45000"));

    public EmailInbox(String host, String port, String email, String emailApplicationPassword, String secureCon) {
        this.host = host;
        this.port = port;
        this.secureCon = secureCon;
        this.email = email;
        this.emailApplicationPassword = emailApplicationPassword;
    }

    public static EmailUtilities.Inbox.EmailMessage getEmail(
            String email,
            String emailApplicationPassword,
            String host,
            String port,
            String secureCon,
            EmailUtilities.Inbox.EmailField filter,
            String filterKey,
            long timeout,
            Boolean print,
            Boolean save,
            Boolean saveAttachments) {
        EmailUtilities.Inbox inbox = getInbox(
                email,
                emailApplicationPassword,
                host,
                port,
                secureCon,
                filter,
                filterKey,
                timeout,
                print,
                save,
                saveAttachments
        );
        return inbox.getMessageBy(filter, filterKey);
    }

    public EmailUtilities.Inbox.EmailMessage getEmail(
            EmailUtilities.Inbox.EmailField filter,
            String filterKey,
            long timeout,
            Boolean print,
            Boolean save,
            Boolean saveAttachments) {
        EmailUtilities.Inbox inbox = getInbox(
                email,
                emailApplicationPassword,
                host,
                port,
                secureCon,
                filter,
                filterKey,
                timeout,
                print,
                save,
                saveAttachments
        );
        return inbox.getMessageBy(filter, filterKey);
    }

    public static void clearInbox(
            String email,
            String emailApplicationPassword,
            String host,
            String port,
            String secureCon
    ) {
        new Printer(EmailInbox.class).info("Flushing email inbox...");
        EmailInbox inbox = new EmailInbox(
                host,
                port,
                email,
                emailApplicationPassword,
                secureCon
        );
        inbox.clearInbox();
    }

    public void clearInbox() {
        log.info("Flushing email inbox...");
        EmailUtilities.Inbox inbox = new EmailUtilities.Inbox(
                host,
                port,
                email,
                emailApplicationPassword,
                secureCon
        );
        inbox.load(false, false, false);
    }

    public static EmailUtilities.Inbox getInbox(
            String email,
            String emailApplicationPassword,
            String host,
            String port,
            String secureCon,
            long timeout,
            Boolean print,
            Boolean save,
            Boolean saveAttachments,
            Pair<EmailUtilities.Inbox.EmailField, String>... filterPairs) {
        double initialTime = System.currentTimeMillis();
        Printer log = new Printer(EmailInbox.class);
        log.info("Acquiring email...");
        EmailUtilities.Inbox inbox = new EmailUtilities.Inbox(
                host,
                port,
                email,
                emailApplicationPassword,
                secureCon
        );
        ReflectionUtilities.iterativeConditionalInvocation(
                30,
                () -> {
                    inbox.load(print, save, saveAttachments, filterPairs);
                    return inbox.messages.size() > 0;
                }
        );
        log.success("Email(s) acquired!");
        return inbox;
    }

    public static EmailUtilities.Inbox getInbox
}
