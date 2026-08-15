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

describe('LigneCommande e2e test', () => {
  const ligneCommandePageUrl = '/ligne-commande';
  const ligneCommandePageUrlPattern = new RegExp('/ligne-commande(\\?.*)?$');
  let username: string;
  let password: string;
  // const ligneCommandeSample = {"quantite":19967,"prixUnitaire":20387.41};

  let ligneCommande;
  // let varianteProduit;
  // let commande;

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
      body: {"stock":750},
    }).then(({ body }) => {
      varianteProduit = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/commandes',
      body: {"numero":"membre du personnel triste parce que","dateCommande":"2026-08-14T22:16:26.248Z","montantTotal":16084.8,"modePaiement":"refaire gens gens","statutCommande":"EXPEDIEE"},
    }).then(({ body }) => {
      commande = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/ligne-commandes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ligne-commandes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ligne-commandes/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/variante-produits', {
      statusCode: 200,
      body: [varianteProduit],
    });

    cy.intercept('GET', '/api/commandes', {
      statusCode: 200,
      body: [commande],
    });

  });
   */

  afterEach(() => {
    if (ligneCommande) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ligne-commandes/${ligneCommande.id}`,
      }).then(() => {
        ligneCommande = undefined;
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
    if (commande) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/commandes/${commande.id}`,
      }).then(() => {
        commande = undefined;
      });
    }
  });
   */

  it('LigneCommandes menu should load LigneCommandes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ligne-commande');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LigneCommande').should('exist');
    cy.url().should('match', ligneCommandePageUrlPattern);
  });

  describe('LigneCommande page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ligneCommandePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LigneCommande page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ligne-commande/new$'));
        cy.getEntityCreateUpdateHeading('LigneCommande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ligneCommandePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ligne-commandes',
          body: {
            ...ligneCommandeSample,
            variante: varianteProduit,
            commande: commande,
          },
        }).then(({ body }) => {
          ligneCommande = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ligne-commandes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ligne-commandes?page=0&size=20>; rel="last",<http://localhost/api/ligne-commandes?page=0&size=20>; rel="first"',
              },
              body: [ligneCommande],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ligneCommandePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(ligneCommandePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details LigneCommande page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ligneCommande');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ligneCommandePageUrlPattern);
      });

      it('edit button click should load edit LigneCommande page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LigneCommande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ligneCommandePageUrlPattern);
      });

      it('edit button click should load edit LigneCommande page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LigneCommande');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ligneCommandePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of LigneCommande', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ligneCommande').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', ligneCommandePageUrlPattern);

        ligneCommande = undefined;
      });
    });
  });

  describe('new LigneCommande page', () => {
    beforeEach(() => {
      cy.visit(ligneCommandePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LigneCommande');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of LigneCommande', () => {
      cy.get(`[data-cy="quantite"]`).type('26018');
      cy.get(`[data-cy="quantite"]`).should('have.value', '26018');

      cy.get(`[data-cy="prixUnitaire"]`).type('11326.43');
      cy.get(`[data-cy="prixUnitaire"]`).should('have.value', '11326.43');

      cy.get(`[data-cy="variante"]`).select(1);
      cy.get(`[data-cy="commande"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        ligneCommande = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', ligneCommandePageUrlPattern);
    });
  });
});
