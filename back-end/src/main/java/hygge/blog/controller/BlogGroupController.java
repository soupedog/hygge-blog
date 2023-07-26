package hygge.blog.controller;

import hygge.blog.controller.doc.BlogGroupControllerDoc;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.BlogGroupDto;
import hygge.blog.domain.local.dto.GroupBindInfo;
import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.domain.local.po.BlogGroup;
import hygge.blog.service.local.normal.BlogGroupServiceImpl;
import hygge.commons.constant.enums.GlobalHyggeCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xavier
 * @date 2022/7/22
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class BlogGroupController implements BlogGroupControllerDoc {
    @Autowired
    private BlogGroupServiceImpl blogGroupService;

    @Override
    @PostMapping("/blogGroup")
    public ResponseEntity<HyggeBlogControllerResponse<BlogGroupDto>> createBlogGroup(@RequestBody BlogGroupDto blogGroupDto) {
        BlogGroup blogGroup = PoDtoMapper.INSTANCE.dtoToPo(blogGroupDto);

        BlogGroup resultTemp = blogGroupService.createCreateBlogGroup(blogGroup);

        BlogGroupDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<BlogGroupDto>>) success(result);
    }

    @Override
    @PostMapping("/blogGroup/admission")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> groupAdmission(@RequestBody GroupBindInfo groupBindInfo) {
        if (blogGroupService.admission(groupBindInfo)) {
            return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success(GlobalHyggeCodeEnum.SUCCESS, "success", null);
        } else {
            return (ResponseEntity<HyggeBlogControllerResponse<Void>>) fail(HttpStatus.OK, null, BlogSystemCode.BLOG_GROUP_BIND_CHANGE_EXCEPTION, "no update", null, null, (HyggeControllerResponseWrapper<ResponseEntity<?>>) LIGHT_ERROR_WRAPPER);
        }
    }

    @Override
    @PostMapping("/blogGroup/eviction")
    public ResponseEntity<HyggeBlogControllerResponse<Void>> groupEviction(@RequestBody GroupBindInfo groupBindInfo) {
        if (blogGroupService.eviction(groupBindInfo)) {
            return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success(GlobalHyggeCodeEnum.SUCCESS, "success", null);
        } else {
            return (ResponseEntity<HyggeBlogControllerResponse<Void>>) success(GlobalHyggeCodeEnum.SUCCESS, "no update", null);
        }
    }
}
