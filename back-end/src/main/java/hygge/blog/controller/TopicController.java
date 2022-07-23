package hygge.blog.controller;

import hygge.blog.controller.doc.TopicControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.TopicDto;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Topic;
import hygge.blog.service.TopicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class TopicController implements TopicControllerDoc {
    @Autowired
    private TopicServiceImpl topicService;

    @Override
    @PostMapping("/topic")
    public ResponseEntity<HyggeBlogControllerResponse<TopicDto>> createTopic(@RequestBody TopicDto topicDto) {
        Topic topic = PoDtoMapper.INSTANCE.dtoToPo(topicDto);
        Topic resultTemp = topicService.createTopic(topic);
        TopicDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<TopicDto>>) success(result);
    }

    @Override
    @PostMapping("/topic/{tid}")
    public ResponseEntity<HyggeBlogControllerResponse<TopicDto>> updateTopic(@PathVariable("tid") String tid,@RequestBody Map<String, Object> data) {
        Topic resultTemp = topicService.updateTopic(tid,data);
        TopicDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<TopicDto>>) success(result);
    }
}
