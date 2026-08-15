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

describe('Commande e2e test', () => {
  const commandePageUrl = '/commande';
  const commandePageUrlPattern = new RegExp('/commande(\\?.*)?$');
  let username: string;
  let password: string;
  // const commandeSample = {"numero":"quand","dateCommande":"2026-08-14T13:10:52.759Z","montantTotal":30306.9,"statutCommande":"ANNULEE"};

  let commande;
  // let livraison;
  // let clienteProfile;

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
      url: '/api/livraisons',
      body: {"adresse":"psitt","ville":"bè commis de manière à ce que","quartier":"sauf à novice","telephone":"0512487456","fraisLivraison":32125.12,"statutLivraison":"EN_PREPARATION"},
    }).then(({ body }) => {
      livraison = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/cliente-profiles',
      body: {"telephone":"+33 794797827","actif":false},
    }).then(({ body }) => {
      clienteProfile = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/commandes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/commandes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/commandes/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/paiements', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/livraisons', {
      statusCode: 200,
      body: [livraison],
    });

    cy.intercept('GET', '/api/ligne-commandes', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/cliente-profiles', {
      statusCode: 200,
      body: [clienteProfile],
    });

  });
   */

  afterEach(() => {
    if (commande) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/commandes/${commande.id}`,
      }).then(() => {
        commande = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (livraison) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/livraisons/${livraison.id}`,
      }).then(() => {
        livraison = undefined;
      });
    }
    if (clienteProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cliente-profiles/${clienteProfile.id}`,
      }).then(() => {
        clienteProfile = undefined;
      });
    }
  });
   */

  it('Commandes menu should load Commandes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('commande');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Commande').should('exist');
    cy.url().should('match', commandePageUrlPattern);
  });

  describe('Commande page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(commandePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Commande page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/commande/new$'));
        cy.getEntityCreateUpdateHeading('Commande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/commandes',
          body: {
            ...commandeSample,
            livraison: livraison,
            clienteProfile: clienteProfile,
          },
        }).then(({ body }) => {
          commande = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/commandes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/commandes?page=0&size=20>; rel="last",<http://localhost/api/commandes?page=0&size=20>; rel="first"',
              },
              body: [commande],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(commandePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(commandePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Commande page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('commande');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });

      it('edit button click should load edit Commande page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Commande');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });

      it('edit button click should load edit Commande page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Commande');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Commande', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('commande').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commandePageUrlPattern);

        commande = undefined;
      });
    });
  });

  describe('new Commande page', () => {
    beforeEach(() => {
      cy.visit(commandePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Commande');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Commande', () => {
      cy.get(`[data-cy="numero"]`).type('faciliter dynamique atchoum');
      cy.get(`[data-cy="numero"]`).should('have.value', 'faciliter dynamique atchoum');

      cy.get(`[data-cy="dateCommande"]`).type('2026-08-14T06:35');
      cy.get(`[data-cy="dateCommande"]`).blur();
      cy.get(`[data-cy="dateCommande"]`).should('have.value', '2026-08-14T06:35');

      cy.get(`[data-cy="montantTotal"]`).type('32485.13');
      cy.get(`[data-cy="montantTotal"]`).should('have.value', '32485.13');

      cy.get(`[data-cy="modePaiement"]`).type('vaste apparemment');
      cy.get(`[data-cy="modePaiement"]`).should('have.value', 'vaste apparemment');

      cy.get(`[data-cy="statutCommande"]`).select('EN_PREPARATION');

      cy.get(`[data-cy="livraison"]`).select(1);
      cy.get(`[data-cy="clienteProfile"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        commande = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', commandePageUrlPattern);
    });
  });
});
