package sn.modiva.app.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Commande;
import sn.modiva.app.repository.CommandeRepository;
import sn.modiva.app.service.CommandeService;
import sn.modiva.app.service.dto.CommandeDTO;
import sn.modiva.app.service.mapper.CommandeMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Commande}.
 */
@Service
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private static final Logger LOG = LoggerFactory.getLogger(CommandeServiceImpl.class);

    private final CommandeRepository commandeRepository;

    private final CommandeMapper commandeMapper;

    public CommandeServiceImpl(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    @Override
    public CommandeDTO save(CommandeDTO commandeDTO) {
        LOG.debug("Request to save Commande : {}", commandeDTO);
        Commande commande = commandeMapper.toEntity(commandeDTO);
        commande = commandeRepository.save(commande);
        return commandeMapper.toDto(commande);
    }

    @Override
    public CommandeDTO update(CommandeDTO commandeDTO) {
        LOG.debug("Request to update Commande : {}", commandeDTO);
        Commande commande = commandeMapper.toEntity(commandeDTO);
        commande = commandeRepository.save(commande);
        return commandeMapper.toDto(commande);
    }

    @Override
    public Optional<CommandeDTO> partialUpdate(CommandeDTO commandeDTO) {
        LOG.debug("Request to partially update Commande : {}", commandeDTO);

        return commandeRepository
            .findById(commandeDTO.getId())
            .map(existingCommande -> {
                commandeMapper.partialUpdate(existingCommande, commandeDTO);

                return existingCommande;
            })
            .map(commandeRepository::save)
            .map(commandeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommandeDTO> findOne(Long id) {
        LOG.debug("Request to get Commande : {}", id);
        return commandeRepository.findById(id).map(commandeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Commande : {}", id);
        commandeRepository.deleteById(id);
    }
}
