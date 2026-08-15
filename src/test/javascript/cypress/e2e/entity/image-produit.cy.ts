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

describe('ImageProduit e2e test', () => {
  const imageProduitPageUrl = '/image-produit';
  const imageProduitPageUrlPattern = new RegExp('/image-produit(\\?.*)?$');
  let username: string;
  let password: string;
  // const imageProduitSample = {"url":"https://affable-conseil-municipal.name"};

  let imageProduit;
  // let produit;

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
      url: '/api/produits',
      body: {"nom":"à bas de","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","prix":11941.55,"statut":"INACTIF"},
    }).then(({ body }) => {
      produit = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/image-produits+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/image-produits').as('postEntityRequest');
    cy.intercept('DELETE', '/api/image-produits/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/produits', {
      statusCode: 200,
      body: [produit],
    });

  });
   */

  afterEach(() => {
    if (imageProduit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/image-produits/${imageProduit.id}`,
      }).then(() => {
        imageProduit = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('ImageProduits menu should load ImageProduits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('image-produit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ImageProduit').should('exist');
    cy.url().should('match', imageProduitPageUrlPattern);
  });

  describe('ImageProduit page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(imageProduitPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ImageProduit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/image-produit/new$'));
        cy.getEntityCreateUpdateHeading('ImageProduit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', imageProduitPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/image-produits',
          body: {
            ...imageProduitSample,
            produit: produit,
          },
        }).then(({ body }) => {
          imageProduit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/image-produits+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/image-produits?page=0&size=20>; rel="last",<http://localhost/api/image-produits?page=0&size=20>; rel="first"',
              },
              body: [imageProduit],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(imageProduitPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(imageProduitPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ImageProduit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('imageProduit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', imageProduitPageUrlPattern);
      });

      it('edit button click should load edit ImageProduit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ImageProduit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', imageProduitPageUrlPattern);
      });

      it('edit button click should load edit ImageProduit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ImageProduit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', imageProduitPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ImageProduit', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('imageProduit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', imageProduitPageUrlPattern);

        imageProduit = undefined;
      });
    });
  });

  describe('new ImageProduit page', () => {
    beforeEach(() => {
      cy.visit(imageProduitPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ImageProduit');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ImageProduit', () => {
      cy.get(`[data-cy="url"]`).type('https://dense-police.name');
      cy.get(`[data-cy="url"]`).should('have.value', 'https://dense-police.name');

      cy.get(`[data-cy="ordre"]`).type('24457');
      cy.get(`[data-cy="ordre"]`).should('have.value', '24457');

      cy.get(`[data-cy="produit"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        imageProduit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', imageProduitPageUrlPattern);
    });
  });
});
