package com.app.cms.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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

    @GetMapping("/active")
    public ResponseEntity<List<Template>> getActiveTemplates(){
        return ResponseEntity.ok(this.templateService.getActiveTemplates());
    }

    @GetMapping
    public ResponseEntity<List<Template>> getTemplatesByType(@RequestParam TemplateType type){
        return ResponseEntity.ok(this.templateService.getTemplatesByType(type));
    }
}
