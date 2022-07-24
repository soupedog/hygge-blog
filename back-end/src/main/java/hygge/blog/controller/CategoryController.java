package hygge.blog.controller;

import hygge.blog.controller.doc.CategoryControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Category;
import hygge.blog.service.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
public class CategoryController implements CategoryControllerDoc {
    @Autowired
    private CategoryServiceImpl categoryService;

    @Override
    @PostMapping("/category")
    public ResponseEntity<HyggeBlogControllerResponse<CategoryDto>> createCategory(@RequestBody CategoryDto categoryDto) {
        Category resultTemp = categoryService.createCategory(categoryDto);
        CategoryDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<CategoryDto>>) success(result);
    }

    @Override
    @PutMapping("/category/{cid}")
    public ResponseEntity<HyggeBlogControllerResponse<CategoryDto>> updateCategory(@PathVariable("cid") String cid, @RequestBody Map<String, Object> data) {
        Category resultTemp = categoryService.updateCategory(cid, data);
        CategoryDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<CategoryDto>>) success(result);
    }
}
