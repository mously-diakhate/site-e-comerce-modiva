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

describe('Panier e2e test', () => {
  const panierPageUrl = '/panier';
  const panierPageUrlPattern = new RegExp('/panier(\\?.*)?$');
  let username: string;
  let password: string;
  const panierSample = {};

  let panier;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/paniers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/paniers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/paniers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (panier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/paniers/${panier.id}`,
      }).then(() => {
        panier = undefined;
      });
    }
  });

  it('Paniers menu should load Paniers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('panier');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Panier').should('exist');
    cy.url().should('match', panierPageUrlPattern);
  });

  describe('Panier page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(panierPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Panier page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/panier/new$'));
        cy.getEntityCreateUpdateHeading('Panier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/paniers',
          body: panierSample,
        }).then(({ body }) => {
          panier = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/paniers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [panier],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(panierPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Panier page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('panier');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });

      it('edit button click should load edit Panier page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Panier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });

      it('edit button click should load edit Panier page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Panier');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);
      });

      it('last delete button click should delete instance of Panier', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('panier').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', panierPageUrlPattern);

        panier = undefined;
      });
    });
  });

  describe('new Panier page', () => {
    beforeEach(() => {
      cy.visit(panierPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Panier');
    });

    it('should create an instance of Panier', () => {
      cy.get(`[data-cy="dateCreation"]`).type('2026-08-14T20:18');
      cy.get(`[data-cy="dateCreation"]`).blur();
      cy.get(`[data-cy="dateCreation"]`).should('have.value', '2026-08-14T20:18');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        panier = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', panierPageUrlPattern);
    });
  });
});
