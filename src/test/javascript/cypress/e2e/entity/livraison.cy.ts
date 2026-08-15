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

describe('Livraison e2e test', () => {
  const livraisonPageUrl = '/livraison';
  const livraisonPageUrlPattern = new RegExp('/livraison(\\?.*)?$');
  let username: string;
  let password: string;
  const livraisonSample = { adresse: 'prout', ville: 'hypocrite secours loufoque', statutLivraison: 'EN_PREPARATION' };

  let livraison;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/livraisons+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/livraisons').as('postEntityRequest');
    cy.intercept('DELETE', '/api/livraisons/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (livraison) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/livraisons/${livraison.id}`,
      }).then(() => {
        livraison = undefined;
      });
    }
  });

  it('Livraisons menu should load Livraisons page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('livraison');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Livraison').should('exist');
    cy.url().should('match', livraisonPageUrlPattern);
  });

  describe('Livraison page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(livraisonPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Livraison page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/livraison/new$'));
        cy.getEntityCreateUpdateHeading('Livraison');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', livraisonPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/livraisons',
          body: livraisonSample,
        }).then(({ body }) => {
          livraison = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/livraisons+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [livraison],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(livraisonPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Livraison page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('livraison');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', livraisonPageUrlPattern);
      });

      it('edit button click should load edit Livraison page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Livraison');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', livraisonPageUrlPattern);
      });

      it('edit button click should load edit Livraison page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Livraison');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', livraisonPageUrlPattern);
      });

      it('last delete button click should delete instance of Livraison', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('livraison').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', livraisonPageUrlPattern);

        livraison = undefined;
      });
    });
  });

  describe('new Livraison page', () => {
    beforeEach(() => {
      cy.visit(livraisonPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Livraison');
    });

    it('should create an instance of Livraison', () => {
      cy.get(`[data-cy="adresse"]`).type('pschitt drelin');
      cy.get(`[data-cy="adresse"]`).should('have.value', 'pschitt drelin');

      cy.get(`[data-cy="ville"]`).type('à demi');
      cy.get(`[data-cy="ville"]`).should('have.value', 'à demi');

      cy.get(`[data-cy="quartier"]`).type('en decà de');
      cy.get(`[data-cy="quartier"]`).should('have.value', 'en decà de');

      cy.get(`[data-cy="telephone"]`).type('+33 775700853');
      cy.get(`[data-cy="telephone"]`).should('have.value', '+33 775700853');

      cy.get(`[data-cy="fraisLivraison"]`).type('9554.31');
      cy.get(`[data-cy="fraisLivraison"]`).should('have.value', '9554.31');

      cy.get(`[data-cy="statutLivraison"]`).select('EN_COURS');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        livraison = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', livraisonPageUrlPattern);
    });
  });
});
