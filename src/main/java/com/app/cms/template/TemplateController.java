package com.app.cms.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<Template> createTemplate(@Valid @RequestBody TemplateDTO dto){
        Template created = templateService.createTemplate(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplate(@PathVariable String id){
        Template template = this.templateService.getTemplate(id);
        return ResponseEntity.ok(template);
    }

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates(){
        return ResponseEntity.ok(this.templateService.getAllTemplates());
    }

    @GetMapping("/byStatus")
    public ResponseEntity<List<Template>> getActiveTemplates(@RequestParam TemplateStatus status){
        return ResponseEntity.ok(this.templateService.getTemplatesByStatus(status));
    }

    @GetMapping("/byType")
    public ResponseEntity<List<Template>> getTemplatesByType(@RequestParam TemplateType type){
        return ResponseEntity.ok(this.templateService.getTemplatesByType(type));
    }

    @GetMapping("/active/{type}")
    public ResponseEntity<List<Template>> getActiveTemplatesByType(@PathVariable TemplateType type){
        return ResponseEntity.ok(this.templateService.getActiveTemplatesByType(type));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Template>> searchTemplates(@PathVariable String keyword){
        return ResponseEntity.ok(this.templateService.searchTemplates(keyword));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable String id, @RequestBody @Valid TemplateDTO request){
        Template updatedTemplate = this.templateService.updateTemplate(id,request);
        return ResponseEntity.ok(updatedTemplate);
    }
}
