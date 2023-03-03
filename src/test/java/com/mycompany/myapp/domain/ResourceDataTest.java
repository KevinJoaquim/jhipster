package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourceDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceData.class);
        ResourceData resourceData1 = new ResourceData();
        resourceData1.setId(1L);
        ResourceData resourceData2 = new ResourceData();
        resourceData2.setId(resourceData1.getId());
        assertThat(resourceData1).isEqualTo(resourceData2);
        resourceData2.setId(2L);
        assertThat(resourceData1).isNotEqualTo(resourceData2);
        resourceData1.setId(null);
        assertThat(resourceData1).isNotEqualTo(resourceData2);
    }
}
