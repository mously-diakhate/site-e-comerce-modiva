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

describe('Produit e2e test', () => {
  const produitPageUrl = '/produit';
  const produitPageUrlPattern = new RegExp('/produit(\\?.*)?$');
  let username: string;
  let password: string;
  const produitSample = { nom: 'large', prix: 8677.71, statut: 'INACTIF' };

  let produit;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/categories',
      body: { nom: 'clac peut-être', description: 'intrépide' },
    }).then(({ body }) => {
      categorie = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/produits+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/produits').as('postEntityRequest');
    cy.intercept('DELETE', '/api/produits/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/image-produits', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/variante-produits', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/promotions', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/categories', {
      statusCode: 200,
      body: [categorie],
    });
  });

  afterEach(() => {
    if (produit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/produits/${produit.id}`,
      }).then(() => {
        produit = undefined;
      });
    }
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

  it('Produits menu should load Produits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('produit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Produit').should('exist');
    cy.url().should('match', produitPageUrlPattern);
  });

  describe('Produit page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(produitPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Produit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/produit/new$'));
        cy.getEntityCreateUpdateHeading('Produit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', produitPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/produits',
          body: {
            ...produitSample,
            categorie,
          },
        }).then(({ body }) => {
          produit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/produits+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/produits?page=0&size=20>; rel="last",<http://localhost/api/produits?page=0&size=20>; rel="first"',
              },
              body: [produit],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(produitPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Produit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('produit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', produitPageUrlPattern);
      });

      it('edit button click should load edit Produit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Produit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', produitPageUrlPattern);
      });

      it('edit button click should load edit Produit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Produit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', produitPageUrlPattern);
      });

      it('last delete button click should delete instance of Produit', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('produit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', produitPageUrlPattern);

        produit = undefined;
      });
    });
  });

  describe('new Produit page', () => {
    beforeEach(() => {
      cy.visit(produitPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Produit');
    });

    it('should create an instance of Produit', () => {
      cy.get(`[data-cy="nom"]`).type('de crainte que comme tellement');
      cy.get(`[data-cy="nom"]`).should('have.value', 'de crainte que comme tellement');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="prix"]`).type('23317.01');
      cy.get(`[data-cy="prix"]`).should('have.value', '23317.01');

      cy.get(`[data-cy="statut"]`).select('ACTIF');

      cy.get(`[data-cy="categorie"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        produit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', produitPageUrlPattern);
    });
  });
});
