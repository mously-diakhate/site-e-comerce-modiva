package sn.modiva.app.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.repository.ClienteProfileRepository;
import sn.modiva.app.service.ClienteProfileService;
import sn.modiva.app.service.dto.ClienteProfileDTO;
import sn.modiva.app.service.mapper.ClienteProfileMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.ClienteProfile}.
 */
@Service
@Transactional
public class ClienteProfileServiceImpl implements ClienteProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(ClienteProfileServiceImpl.class);

    private final ClienteProfileRepository clienteProfileRepository;

    private final ClienteProfileMapper clienteProfileMapper;

    public ClienteProfileServiceImpl(ClienteProfileRepository clienteProfileRepository, ClienteProfileMapper clienteProfileMapper) {
        this.clienteProfileRepository = clienteProfileRepository;
        this.clienteProfileMapper = clienteProfileMapper;
    }

    @Override
    public ClienteProfileDTO save(ClienteProfileDTO clienteProfileDTO) {
        LOG.debug("Request to save ClienteProfile : {}", clienteProfileDTO);
        ClienteProfile clienteProfile = clienteProfileMapper.toEntity(clienteProfileDTO);
        clienteProfile = clienteProfileRepository.save(clienteProfile);
        return clienteProfileMapper.toDto(clienteProfile);
    }

    @Override
    public ClienteProfileDTO update(ClienteProfileDTO clienteProfileDTO) {
        LOG.debug("Request to update ClienteProfile : {}", clienteProfileDTO);
        ClienteProfile clienteProfile = clienteProfileMapper.toEntity(clienteProfileDTO);
        clienteProfile = clienteProfileRepository.save(clienteProfile);
        return clienteProfileMapper.toDto(clienteProfile);
    }

    @Override
    public Optional<ClienteProfileDTO> partialUpdate(ClienteProfileDTO clienteProfileDTO) {
        LOG.debug("Request to partially update ClienteProfile : {}", clienteProfileDTO);

        return clienteProfileRepository
            .findById(clienteProfileDTO.getId())
            .map(existingClienteProfile -> {
                clienteProfileMapper.partialUpdate(existingClienteProfile, clienteProfileDTO);

                return existingClienteProfile;
            })
            .map(clienteProfileRepository::save)
            .map(clienteProfileMapper::toDto);
    }

    public Page<ClienteProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return clienteProfileRepository.findAllWithEagerRelationships(pageable).map(clienteProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteProfileDTO> findOne(Long id) {
        LOG.debug("Request to get ClienteProfile : {}", id);
        return clienteProfileRepository.findOneWithEagerRelationships(id).map(clienteProfileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ClienteProfile : {}", id);
        clienteProfileRepository.deleteById(id);
    }
}
