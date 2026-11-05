package com.hellmetz.festival.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class FlashMessages {

    private final MessageSource messageSource;

    public FlashMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void succes(RedirectAttributes ra, String key) {
        ra.addFlashAttribute("succes", messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
    }

    public void erreur(RedirectAttributes ra, String key) {
        ra.addFlashAttribute("erreur", messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
    }

    public String get(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
