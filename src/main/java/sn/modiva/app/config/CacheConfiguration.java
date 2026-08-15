package sn.modiva.app.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        var ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, sn.modiva.app.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, sn.modiva.app.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, sn.modiva.app.domain.User.class.getName());
            createCache(cm, sn.modiva.app.domain.Authority.class.getName());
            createCache(cm, sn.modiva.app.domain.User.class.getName() + ".authorities");
            createCache(cm, sn.modiva.app.domain.ClienteProfile.class.getName());
            createCache(cm, sn.modiva.app.domain.ClienteProfile.class.getName() + ".adresses");
            createCache(cm, sn.modiva.app.domain.ClienteProfile.class.getName() + ".commandes");
            createCache(cm, sn.modiva.app.domain.Adresse.class.getName());
            createCache(cm, sn.modiva.app.domain.Categorie.class.getName());
            createCache(cm, sn.modiva.app.domain.Categorie.class.getName() + ".produits");
            createCache(cm, sn.modiva.app.domain.Produit.class.getName());
            createCache(cm, sn.modiva.app.domain.Produit.class.getName() + ".images");
            createCache(cm, sn.modiva.app.domain.Produit.class.getName() + ".variantes");
            createCache(cm, sn.modiva.app.domain.Produit.class.getName() + ".promotions");
            createCache(cm, sn.modiva.app.domain.ImageProduit.class.getName());
            createCache(cm, sn.modiva.app.domain.Taille.class.getName());
            createCache(cm, sn.modiva.app.domain.Couleur.class.getName());
            createCache(cm, sn.modiva.app.domain.VarianteProduit.class.getName());
            createCache(cm, sn.modiva.app.domain.Panier.class.getName());
            createCache(cm, sn.modiva.app.domain.Panier.class.getName() + ".lignes");
            createCache(cm, sn.modiva.app.domain.LignePanier.class.getName());
            createCache(cm, sn.modiva.app.domain.Commande.class.getName());
            createCache(cm, sn.modiva.app.domain.Commande.class.getName() + ".lignes");
            createCache(cm, sn.modiva.app.domain.LigneCommande.class.getName());
            createCache(cm, sn.modiva.app.domain.Paiement.class.getName());
            createCache(cm, sn.modiva.app.domain.Livraison.class.getName());
            createCache(cm, sn.modiva.app.domain.Promotion.class.getName());
            createCache(cm, sn.modiva.app.domain.Promotion.class.getName() + ".produits");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
