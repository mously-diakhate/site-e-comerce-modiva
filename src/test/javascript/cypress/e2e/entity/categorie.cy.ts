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

describe('Categorie e2e test', () => {
  const categoriePageUrl = '/categorie';
  const categoriePageUrlPattern = new RegExp('/categorie(\\?.*)?$');
  let username: string;
  let password: string;
  const categorieSample = { nom: 'candide' };

  let categorie;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/categories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (categorie) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/categories/${categorie.id}`,
      }).then(() => {
        categorie = undefined;
      });
    }
  });

  it('Categories menu should load Categories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('categorie');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Categorie').should('exist');
    cy.url().should('match', categoriePageUrlPattern);
  });

  describe('Categorie page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(categoriePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Categorie page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/categorie/new$'));
        cy.getEntityCreateUpdateHeading('Categorie');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoriePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/categories',
          body: categorieSample,
        }).then(({ body }) => {
          categorie = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/categories?page=0&size=20>; rel="last",<http://localhost/api/categories?page=0&size=20>; rel="first"',
              },
              body: [categorie],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(categoriePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Categorie page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('categorie');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoriePageUrlPattern);
      });

      it('edit button click should load edit Categorie page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Categorie');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoriePageUrlPattern);
      });

      it('edit button click should load edit Categorie page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Categorie');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoriePageUrlPattern);
      });

      it('last delete button click should delete instance of Categorie', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('categorie').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', categoriePageUrlPattern);

        categorie = undefined;
      });
    });
  });

  describe('new Categorie page', () => {
    beforeEach(() => {
      cy.visit(categoriePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Categorie');
    });

    it('should create an instance of Categorie', () => {
      cy.get(`[data-cy="nom"]`).type('puis');
      cy.get(`[data-cy="nom"]`).should('have.value', 'puis');

      cy.get(`[data-cy="description"]`).type('dring soustraire');
      cy.get(`[data-cy="description"]`).should('have.value', 'dring soustraire');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        categorie = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', categoriePageUrlPattern);
    });
  });
});
