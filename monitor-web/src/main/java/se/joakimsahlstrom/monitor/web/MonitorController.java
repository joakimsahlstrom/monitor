package se.joakimsahlstrom.monitor.web;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MonitorController {

    private final RestTemplate rt = restTemplate(); // no DI for now

    private static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM}));

        ArrayList<HttpMessageConverter<?>> converters = new ArrayList<>(restTemplate.getMessageConverters());
        converters.add(converter);
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }

    @GetMapping("/services")
    public String getServices(Model model) {
        ServicesView services2 = rt.getForObject("http://localhost:8081/service", ServicesView.class);

        model.addAttribute("services", services2.getServices());
        model.addAttribute("newService", new NewService());
        model.addAttribute("deleteService", new DeleteService());

        return "services";
    }

    @PostMapping("/add")
    public @ResponseBody String addService(@ModelAttribute NewService newService, Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("name", newService.getName());
        map.add("url", newService.getUrl());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String id = rt.postForObject("http://localhost:8081/service", request, String.class);
        return "added";
    }

    @PostMapping("/delete")
    public @ResponseBody String deleteService(@ModelAttribute DeleteService id, Model model) {
        rt.delete("http://localhost:8081/service/" + id.getId());

        return "deleted";
    }


    // View classes

    // Form
    static class NewService {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    static class DeleteService {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    static class ServicesView {
        private List<ServiceView> services;

        public List<ServiceView> getServices() {
            return services;
        }

        public void setServices(List<ServiceView> services) {
            this.services = services;
        }
    }

    static class ServiceView {
        private String id;
        private String name;
        private String url;
        private String status;
        private String lastCheck;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLastCheck() {
            return lastCheck;
        }

        public void setLastCheck(String lastCheck) {
            this.lastCheck = lastCheck;
        }
    }

}
