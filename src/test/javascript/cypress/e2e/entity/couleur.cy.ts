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

describe('Couleur e2e test', () => {
  const couleurPageUrl = '/couleur';
  const couleurPageUrlPattern = new RegExp('/couleur(\\?.*)?$');
  let username: string;
  let password: string;
  const couleurSample = { libelle: 'malgré' };

  let couleur;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/couleurs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/couleurs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/couleurs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (couleur) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/couleurs/${couleur.id}`,
      }).then(() => {
        couleur = undefined;
      });
    }
  });

  it('Couleurs menu should load Couleurs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('couleur');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Couleur').should('exist');
    cy.url().should('match', couleurPageUrlPattern);
  });

  describe('Couleur page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(couleurPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Couleur page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/couleur/new$'));
        cy.getEntityCreateUpdateHeading('Couleur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', couleurPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/couleurs',
          body: couleurSample,
        }).then(({ body }) => {
          couleur = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/couleurs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [couleur],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(couleurPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Couleur page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('couleur');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', couleurPageUrlPattern);
      });

      it('edit button click should load edit Couleur page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Couleur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', couleurPageUrlPattern);
      });

      it('edit button click should load edit Couleur page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Couleur');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', couleurPageUrlPattern);
      });

      it('last delete button click should delete instance of Couleur', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('couleur').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', couleurPageUrlPattern);

        couleur = undefined;
      });
    });
  });

  describe('new Couleur page', () => {
    beforeEach(() => {
      cy.visit(couleurPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Couleur');
    });

    it('should create an instance of Couleur', () => {
      cy.get(`[data-cy="libelle"]`).type('parmi tandis que à demi');
      cy.get(`[data-cy="libelle"]`).should('have.value', 'parmi tandis que à demi');

      cy.get(`[data-cy="codeHex"]`).type('aimable diablement ha ha');
      cy.get(`[data-cy="codeHex"]`).should('have.value', 'aimable diablement ha ha');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        couleur = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', couleurPageUrlPattern);
    });
  });
});
