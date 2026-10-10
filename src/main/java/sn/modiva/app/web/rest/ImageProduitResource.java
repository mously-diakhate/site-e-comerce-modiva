package sn.modiva.app.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.modiva.app.repository.ImageProduitRepository;
import sn.modiva.app.service.ImageProduitService;
import sn.modiva.app.service.dto.ImageProduitDTO;
import sn.modiva.app.service.mapper.ImageProduitMapper; // NEW
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.ImageProduit}.
 */
@RestController
@RequestMapping("/api/image-produits")
public class ImageProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(ImageProduitResource.class);

    private static final String ENTITY_NAME = "imageProduit";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final ImageProduitService imageProduitService;

    private final ImageProduitRepository imageProduitRepository;

    private final ImageProduitMapper imageProduitMapper; // NEW

    public ImageProduitResource(
        ImageProduitService imageProduitService,
        ImageProduitRepository imageProduitRepository,
        ImageProduitMapper imageProduitMapper // NEW
    ) {
        this.imageProduitService = imageProduitService;
        this.imageProduitRepository = imageProduitRepository;
        this.imageProduitMapper = imageProduitMapper; // NEW
    }

    @PostMapping("")
    public ResponseEntity<ImageProduitDTO> createImageProduit(@Valid @RequestBody ImageProduitDTO imageProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ImageProduit : {}", imageProduitDTO);
        if (imageProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new imageProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        imageProduitDTO = imageProduitService.save(imageProduitDTO);
        return ResponseEntity.created(new URI("/api/image-produits/" + imageProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, imageProduitDTO.getId().toString()))
            .body(imageProduitDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImageProduitDTO> updateImageProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImageProduitDTO imageProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ImageProduit : {}, {}", id, imageProduitDTO);
        if (imageProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, imageProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!imageProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        imageProduitDTO = imageProduitService.update(imageProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, imageProduitDTO.getId().toString()))
            .body(imageProduitDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ImageProduitDTO> partialUpdateImageProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImageProduitDTO imageProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ImageProduit partially : {}, {}", id, imageProduitDTO);
        if (imageProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, imageProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!imageProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ImageProduitDTO> result = imageProduitService.partialUpdate(imageProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, imageProduitDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<ImageProduitDTO>> getAllImageProduits(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ImageProduits");
        Page<ImageProduitDTO> page = imageProduitService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /image-produits/by-produit/:produitId} : get all images for a given produit, ordered by 'ordre'.
     *
     * @param produitId the id of the produit.
     * @return the list of ImageProduitDTO belonging to that produit.
     */
    @GetMapping("/by-produit/{produitId}") // NEW
    public List<ImageProduitDTO> getImagesByProduit(@PathVariable("produitId") Long produitId) {
        LOG.debug("REST request to get ImageProduits for produit : {}", produitId);
        return imageProduitRepository.findByProduitIdOrderByOrdreAsc(produitId).stream().map(imageProduitMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageProduitDTO> getImageProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ImageProduit : {}", id);
        Optional<ImageProduitDTO> imageProduitDTO = imageProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(imageProduitDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImageProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ImageProduit : {}", id);
        imageProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
