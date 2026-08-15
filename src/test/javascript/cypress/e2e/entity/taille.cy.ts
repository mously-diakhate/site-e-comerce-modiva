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

describe('Taille e2e test', () => {
  const taillePageUrl = '/taille';
  const taillePageUrlPattern = new RegExp('/taille(\\?.*)?$');
  let username: string;
  let password: string;
  const tailleSample = { libelle: 'sauf à sous couleur de vivace' };

  let taille;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tailles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tailles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tailles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (taille) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tailles/${taille.id}`,
      }).then(() => {
        taille = undefined;
      });
    }
  });

  it('Tailles menu should load Tailles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('taille');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Taille').should('exist');
    cy.url().should('match', taillePageUrlPattern);
  });

  describe('Taille page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(taillePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Taille page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/taille/new$'));
        cy.getEntityCreateUpdateHeading('Taille');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taillePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tailles',
          body: tailleSample,
        }).then(({ body }) => {
          taille = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tailles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [taille],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(taillePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Taille page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('taille');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taillePageUrlPattern);
      });

      it('edit button click should load edit Taille page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Taille');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taillePageUrlPattern);
      });

      it('edit button click should load edit Taille page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Taille');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taillePageUrlPattern);
      });

      it('last delete button click should delete instance of Taille', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('taille').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', taillePageUrlPattern);

        taille = undefined;
      });
    });
  });

  describe('new Taille page', () => {
    beforeEach(() => {
      cy.visit(taillePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Taille');
    });

    it('should create an instance of Taille', () => {
      cy.get(`[data-cy="libelle"]`).type('vlan toujours');
      cy.get(`[data-cy="libelle"]`).should('have.value', 'vlan toujours');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        taille = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', taillePageUrlPattern);
    });
  });
});
