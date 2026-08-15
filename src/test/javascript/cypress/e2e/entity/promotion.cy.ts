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

describe('Promotion e2e test', () => {
  const promotionPageUrl = '/promotion';
  const promotionPageUrlPattern = new RegExp('/promotion(\\?.*)?$');
  let username: string;
  let password: string;
  const promotionSample = {
    nom: 'ronron quoique personnel',
    reduction: 13261.61,
    dateDebut: '2026-08-14T09:47:02.144Z',
    dateFin: '2026-08-14T15:35:31.372Z',
  };

  let promotion;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/promotions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/promotions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/promotions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (promotion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/promotions/${promotion.id}`,
      }).then(() => {
        promotion = undefined;
      });
    }
  });

  it('Promotions menu should load Promotions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('promotion');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Promotion').should('exist');
    cy.url().should('match', promotionPageUrlPattern);
  });

  describe('Promotion page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(promotionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Promotion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/promotion/new$'));
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/promotions',
          body: promotionSample,
        }).then(({ body }) => {
          promotion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/promotions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [promotion],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(promotionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Promotion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('promotion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('edit button click should load edit Promotion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('edit button click should load edit Promotion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('last delete button click should delete instance of Promotion', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('promotion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);

        promotion = undefined;
      });
    });
  });

  describe('new Promotion page', () => {
    beforeEach(() => {
      cy.visit(promotionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Promotion');
    });

    it('should create an instance of Promotion', () => {
      cy.get(`[data-cy="nom"]`).type('en énorme divinement');
      cy.get(`[data-cy="nom"]`).should('have.value', 'en énorme divinement');

      cy.get(`[data-cy="reduction"]`).type('3642.64');
      cy.get(`[data-cy="reduction"]`).should('have.value', '3642.64');

      cy.get(`[data-cy="dateDebut"]`).type('2026-08-14T12:35');
      cy.get(`[data-cy="dateDebut"]`).blur();
      cy.get(`[data-cy="dateDebut"]`).should('have.value', '2026-08-14T12:35');

      cy.get(`[data-cy="dateFin"]`).type('2026-08-14T19:43');
      cy.get(`[data-cy="dateFin"]`).blur();
      cy.get(`[data-cy="dateFin"]`).should('have.value', '2026-08-14T19:43');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        promotion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', promotionPageUrlPattern);
    });
  });
});
