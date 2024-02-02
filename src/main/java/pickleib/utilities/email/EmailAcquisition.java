package pickleib.utilities.email;

import utils.EmailUtilities;
import utils.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.highlighted;

@SuppressWarnings("unused")
public class EmailAcquisition {

    StringUtilities strUtils = new StringUtilities();
    EmailInbox emailInbox;

    public EmailAcquisition(EmailInbox emailInbox){
        this.emailInbox = emailInbox;
    }

    public String acquireEmail(EmailUtilities.Inbox.EmailField filterType, String filterKey) {
        emailInbox.log.info("Acquiring & saving email(s) by " +
                highlighted(BLUE, filterType.name()) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, filterKey)
        );
        emailInbox.getEmail(filterType, filterKey, emailInbox.emailAcquisitionTimeout, false, true, true);
        File dir = new File("inbox");
        String absolutePath = null;
        for (File email : Objects.requireNonNull(dir.listFiles()))
            try {
                boolean nullCheck = Files.probeContentType(email.toPath()) != null;
                if (nullCheck && Files.probeContentType(email.toPath()).equals("text/html")) {
                    absolutePath = "file://" + email.getAbsolutePath().replaceAll("#", "%23");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return absolutePath;
    }

    public String acquireEmail(EmailUtilities.Inbox.EmailField filterType, String filterKey, long timeout) {
        emailInbox.log.info("Acquiring & saving email(s) by " +
                highlighted(BLUE, filterType.name()) +
                highlighted(GRAY, " -> ") +
                highlighted(BLUE, filterKey)
        );
        emailInbox.getEmail(filterType, filterKey, timeout, false, true, true);
        File dir = new File("inbox");
        String absolutePath = null;
        for (File email : Objects.requireNonNull(dir.listFiles()))
            try {
                boolean nullCheck = Files.probeContentType(email.toPath()) != null;
                if (nullCheck && Files.probeContentType(email.toPath()).equals("text/html")) {
                    absolutePath = "file://" + email.getAbsolutePath().replaceAll("#", "%23");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return absolutePath;
    }
}
