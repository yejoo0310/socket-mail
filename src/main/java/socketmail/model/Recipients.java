package socketmail.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Recipients {
    private final List<EmailAddress> addresses;

    public Recipients(String rawAddresses) {
        this.addresses = Arrays.stream(rawAddresses.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(EmailAddress::new)
                .collect(Collectors.toList());
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("Recipients cannot be empty.");
        }
    }

    public Recipients(List<EmailAddress> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException("Recipients cannot be empty.");
        }
        this.addresses = addresses;
    }

    public List<EmailAddress> getAddresses() {
        return addresses;
    }

    public String toHeaderString() {
        return addresses.stream()
                .map(EmailAddress::value)
                .collect(Collectors.joining(", "));
    }
}
