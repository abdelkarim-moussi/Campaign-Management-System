package com.app.cms.template.controller;

import com.app.cms.template.dto.TemplateDTO;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.entity.Template;
import com.app.cms.template.entity.TemplateStatus;
import com.app.cms.template.entity.TemplateType;
import com.app.cms.template.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Template> createTemplate(@Valid @RequestBody TemplateDTO dto){
        Template created = templateService.createTemplate(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Template> getTemplate(@PathVariable Long id){
        Template template = templateService.getTemplate(id);
        return ResponseEntity.ok(template);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Template>> getAllTemplates(
            @RequestParam(required = false) TemplateType type,
            @RequestParam(required = false) TemplateStatus status,
            Pageable pageable){

        if(type != null && status != null){
            return ResponseEntity.ok(
                    templateService.getTemplatesByStatus(status, pageable)
            );
        }else if(type != null){
            return ResponseEntity.ok(templateService.getTemplatesByType(type, pageable));
        }else if(status != null){
            return ResponseEntity.ok(templateService.getTemplatesByStatus(status, pageable));
        }

        return ResponseEntity.ok(templateService.getAllTemplates(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Template> updateTemplate(@PathVariable Long id, @RequestBody @Valid TemplateDTO request){
        Template updatedTemplate = templateService.updateTemplate(id,request);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id){
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Template>> getActiveTemplates(@RequestParam(required = false) TemplateType type, Pageable pageable){
        if(type != null){
            return ResponseEntity.ok(templateService.getActiveTemplatesByType(type, pageable));
        }
        return ResponseEntity.ok(templateService.getActiveTemplates(pageable));
    }

    @GetMapping("/{id}/variables")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<List<String>> getTemplateVariables(@PathVariable Long id){
        return ResponseEntity.ok(
                templateService.getTemplateVariables(id)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Template> activateTemplate(@PathVariable Long id){
        return ResponseEntity.ok(
                templateService.activateTemplate(id)
        );
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Template> archiveTemplate(@PathVariable Long id){
        return ResponseEntity.ok(
                templateService.archiveTemplate(id)
        );
    }

    @GetMapping("/search/{keyword}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Template>> searchTemplates(@PathVariable String keyword, Pageable pageable){
        return ResponseEntity.ok(templateService.searchTemplates(keyword, pageable));
    }

    @GetMapping("/{id}/preview")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<TemplatePreviewResult> previewTemplate(
            @PathVariable Long id,
            @RequestBody Map<String,String> variables){
        return ResponseEntity.ok(
                templateService.previewTemplate(id,variables)
        );
    }
}
