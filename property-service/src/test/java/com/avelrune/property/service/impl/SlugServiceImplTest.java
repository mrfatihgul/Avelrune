package com.avelrune.property.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugServiceImplTest {

    private final SlugServiceImpl slugService = new SlugServiceImpl();

    @Test
    void slugifiesTurkishTitle() {
        assertEquals("kadikoy-de-2-1", slugService.slugify("Kadıköy'de 2+1"));
        assertEquals("istanbul", slugService.slugify("İstanbul"));
    }
}
