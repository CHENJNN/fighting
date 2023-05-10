package com.example.fighting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TestSecurityLogin {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    //要在測試類測security要加入此設定
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testLogin() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")//登入打的url
                        .param("username","jn")//表單內資料設定
                        .param("password","123")
                )
                .andExpect(status().is3xxRedirection())//預期回應3xx httpStatusCode
                .andExpect(redirectedUrl("/"))//收到3xx回應表示要重導頁面, 預期重導至" / "
                .andReturn();//回傳結果

        MockHttpServletResponse response = result.getResponse();
        Map<String, String> headers = response.getHeaderNames()
                .stream()
                .collect(Collectors.toMap(h -> h, response::getHeader));
        String reponseBody = response.getContentAsString();
        // Print headers & body
        System.err.println("------------------------------------------------------");
        System.err.println("ReponseHeader：");
        System.err.println("-");
        headers.forEach((name, value) -> System.err.println(name + ": " + value));
        System.err.println("------------------------------------------------------");
        System.err.println("ReponseBody：");
        System.err.println("-");
        System.err.println(reponseBody);
        System.err.println("------------------------------------------------------");
    }

}
