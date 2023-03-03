package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourceGotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceGot.class);
        ResourceGot resourceGot1 = new ResourceGot();
        resourceGot1.setId(1L);
        ResourceGot resourceGot2 = new ResourceGot();
        resourceGot2.setId(resourceGot1.getId());
        assertThat(resourceGot1).isEqualTo(resourceGot2);
        resourceGot2.setId(2L);
        assertThat(resourceGot1).isNotEqualTo(resourceGot2);
        resourceGot1.setId(null);
        assertThat(resourceGot1).isNotEqualTo(resourceGot2);
    }
}
