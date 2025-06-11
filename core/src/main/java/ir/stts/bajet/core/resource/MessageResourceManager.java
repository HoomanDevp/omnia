package ir.stts.bajet.core.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResourceManager {

    private final MessageSource messageSource;

    public String getMessage(String key) {
        return getMessage(key, (String) null);
    }

    public String getMessage(String key, String... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}