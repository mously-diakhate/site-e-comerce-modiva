package sn.modiva.app.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.ImageProduit;
import sn.modiva.app.repository.ImageProduitRepository;
import sn.modiva.app.service.dto.ImageProduitDTO;
import sn.modiva.app.service.mapper.ImageProduitMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.ImageProduit}.
 */
@Service
@Transactional
public class ImageProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageProduitService.class);

    private final ImageProduitRepository imageProduitRepository;

    private final ImageProduitMapper imageProduitMapper;

    public ImageProduitService(ImageProduitRepository imageProduitRepository, ImageProduitMapper imageProduitMapper) {
        this.imageProduitRepository = imageProduitRepository;
        this.imageProduitMapper = imageProduitMapper;
    }

    /**
     * Save a imageProduit.
     *
     * @param imageProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public ImageProduitDTO save(ImageProduitDTO imageProduitDTO) {
        LOG.debug("Request to save ImageProduit : {}", imageProduitDTO);
        ImageProduit imageProduit = imageProduitMapper.toEntity(imageProduitDTO);
        imageProduit = imageProduitRepository.save(imageProduit);
        return imageProduitMapper.toDto(imageProduit);
    }

    /**
     * Update a imageProduit.
     *
     * @param imageProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public ImageProduitDTO update(ImageProduitDTO imageProduitDTO) {
        LOG.debug("Request to update ImageProduit : {}", imageProduitDTO);
        ImageProduit imageProduit = imageProduitMapper.toEntity(imageProduitDTO);
        imageProduit = imageProduitRepository.save(imageProduit);
        return imageProduitMapper.toDto(imageProduit);
    }

    /**
     * Partially update a imageProduit.
     *
     * @param imageProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ImageProduitDTO> partialUpdate(ImageProduitDTO imageProduitDTO) {
        LOG.debug("Request to partially update ImageProduit : {}", imageProduitDTO);

        return imageProduitRepository
            .findById(imageProduitDTO.getId())
            .map(existingImageProduit -> {
                imageProduitMapper.partialUpdate(existingImageProduit, imageProduitDTO);

                return existingImageProduit;
            })
            .map(imageProduitRepository::save)
            .map(imageProduitMapper::toDto);
    }

    /**
     * Get all the imageProduits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ImageProduitDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ImageProduits");
        return imageProduitRepository.findAll(pageable).map(imageProduitMapper::toDto);
    }

    /**
     * Get one imageProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ImageProduitDTO> findOne(Long id) {
        LOG.debug("Request to get ImageProduit : {}", id);
        return imageProduitRepository.findById(id).map(imageProduitMapper::toDto);
    }

    /**
     * Delete the imageProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ImageProduit : {}", id);
        imageProduitRepository.deleteById(id);
    }
}
