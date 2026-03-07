package com.app.cms.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Template> getTemplate(@PathVariable Long id){
        Template template = templateService.getTemplate(id);
        return ResponseEntity.ok(template);
    }

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates(
            @RequestParam(required = false) TemplateType type,
            @RequestParam(required = false) TemplateStatus status){

        if(type != null && status != null){
            return ResponseEntity.ok(
                    templateService.getTemplatesByStatus(status)
                            .stream()
                            .filter(t -> t.getType().equals(type))
                            .toList()
            );
        }else if(type != null){
            return ResponseEntity.ok(templateService.getTemplatesByType(type));
        }else if(status != null){
            return ResponseEntity.ok(templateService.getTemplatesByStatus(status));
        }

        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable Long id, @RequestBody @Valid TemplateDTO request){
        Template updatedTemplate = templateService.updateTemplate(id,request);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id){
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Template>> getActiveTemplates(@RequestParam(required = false) TemplateType type){
        if(type != null){
            return ResponseEntity.ok(templateService.getActiveTemplatesByType(type));
        }
        return ResponseEntity.ok(templateService.getActiveTemplates());
    }

    @GetMapping("/{id}/variables")
    public ResponseEntity<List<String>> getTemplateVariables(@PathVariable Long id){
        return ResponseEntity.ok(
                templateService.getTemplateVariables(id)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Template> activateTemplate(@PathVariable Long id){
        return ResponseEntity.ok(
                templateService.activateTemplate(id)
        );
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Template> archiveTemplate(@PathVariable Long id){
        return ResponseEntity.ok(
                templateService.archiveTemplate(id)
        );
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Template>> searchTemplates(@PathVariable String keyword){
        return ResponseEntity.ok(templateService.searchTemplates(keyword));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<TemplatePreviewResult> previewTemplate(
            @PathVariable Long id,
            @RequestBody Map<String,String> variables){
        return ResponseEntity.ok(
                templateService.previewTemplate(id,variables)
        );
    }
}
