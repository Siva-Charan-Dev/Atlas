package io.atlas.kv;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtlasKVTest {

    @Test
    void projectBuildsAndTestsExecute() {
        assertThat(AtlasKV.class.getSimpleName()).isEqualTo("AtlasKV");
    }
}
