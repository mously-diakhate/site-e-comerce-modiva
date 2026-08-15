import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('LignePanier e2e test', () => {
  const lignePanierPageUrl = '/ligne-panier';
  const lignePanierPageUrlPattern = new RegExp('/ligne-panier(\\?.*)?$');
  let username: string;
  let password: string;
  // const lignePanierSample = {"quantite":11248};

  let lignePanier;
  // let varianteProduit;
  // let panier;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/variante-produits',
      body: {"stock":22036},
    }).then(({ body }) => {
      varianteProduit = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/paniers',
      body: {"dateCreation":"2026-08-14T08:30:42.545Z"},
    }).then(({ body }) => {
      panier = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/ligne-paniers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ligne-paniers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ligne-paniers/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/variante-produits', {
      statusCode: 200,
      body: [varianteProduit],
    });

    cy.intercept('GET', '/api/paniers', {
      statusCode: 200,
      body: [panier],
    });

  });
   */

  afterEach(() => {
    if (lignePanier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ligne-paniers/${lignePanier.id}`,
      }).then(() => {
        lignePanier = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (varianteProduit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/variante-produits/${varianteProduit.id}`,
      }).then(() => {
        varianteProduit = undefined;
      });
    }
    if (panier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/paniers/${panier.id}`,
      }).then(() => {
        panier = undefined;
      });
    }
  });
   */

  it('LignePaniers menu should load LignePaniers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ligne-panier');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LignePanier').should('exist');
    cy.url().should('match', lignePanierPageUrlPattern);
  });

  describe('LignePanier page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(lignePanierPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LignePanier page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ligne-panier/new$'));
        cy.getEntityCreateUpdateHeading('LignePanier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', lignePanierPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ligne-paniers',
          body: {
            ...lignePanierSample,
            variante: varianteProduit,
            panier: panier,
          },
        }).then(({ body }) => {
          lignePanier = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ligne-paniers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ligne-paniers?page=0&size=20>; rel="last",<http://localhost/api/ligne-paniers?page=0&size=20>; rel="first"',
              },
              body: [lignePanier],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(lignePanierPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(lignePanierPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details LignePanier page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('lignePanier');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', lignePanierPageUrlPattern);
      });

      it('edit button click should load edit LignePanier page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LignePanier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', lignePanierPageUrlPattern);
      });

      it('edit button click should load edit LignePanier page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LignePanier');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', lignePanierPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of LignePanier', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('lignePanier').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', lignePanierPageUrlPattern);

        lignePanier = undefined;
      });
    });
  });

  describe('new LignePanier page', () => {
    beforeEach(() => {
      cy.visit(lignePanierPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LignePanier');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of LignePanier', () => {
      cy.get(`[data-cy="quantite"]`).type('15883');
      cy.get(`[data-cy="quantite"]`).should('have.value', '15883');

      cy.get(`[data-cy="variante"]`).select(1);
      cy.get(`[data-cy="panier"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        lignePanier = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', lignePanierPageUrlPattern);
    });
  });
});
