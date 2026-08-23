package com.avelrune.property.service.impl;

import com.avelrune.property.service.SlugService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class SlugServiceImpl implements SlugService {

    @Override
    public String slugify(String input) {
        String value = input.toLowerCase(Locale.forLanguageTag("tr-TR"))
                .replace("ı", "i")
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ö", "o")
                .replace("ç", "c");
        value = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        value = value.replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return value;
    }
}
