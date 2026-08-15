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

describe('VarianteProduit e2e test', () => {
  const varianteProduitPageUrl = '/variante-produit';
  const varianteProduitPageUrlPattern = new RegExp('/variante-produit(\\?.*)?$');
  let username: string;
  let password: string;
  // const varianteProduitSample = {"stock":20782};

  let varianteProduit;
  // let taille;
  // let couleur;
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
      url: '/api/tailles',
      body: {"libelle":"téléphoner en outre de membre titulaire"},
    }).then(({ body }) => {
      taille = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/couleurs',
      body: {"libelle":"soit","codeHex":"parlementaire dès"},
    }).then(({ body }) => {
      couleur = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/produits',
      body: {"nom":"police patientèle hirsute","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","prix":2092.79,"statut":"INACTIF"},
    }).then(({ body }) => {
      produit = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/variante-produits+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/variante-produits').as('postEntityRequest');
    cy.intercept('DELETE', '/api/variante-produits/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/tailles', {
      statusCode: 200,
      body: [taille],
    });

    cy.intercept('GET', '/api/couleurs', {
      statusCode: 200,
      body: [couleur],
    });

    cy.intercept('GET', '/api/produits', {
      statusCode: 200,
      body: [produit],
    });

  });
   */

  afterEach(() => {
    if (varianteProduit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/variante-produits/${varianteProduit.id}`,
      }).then(() => {
        varianteProduit = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (taille) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tailles/${taille.id}`,
      }).then(() => {
        taille = undefined;
      });
    }
    if (couleur) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/couleurs/${couleur.id}`,
      }).then(() => {
        couleur = undefined;
      });
    }
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

  it('VarianteProduits menu should load VarianteProduits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('variante-produit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('VarianteProduit').should('exist');
    cy.url().should('match', varianteProduitPageUrlPattern);
  });

  describe('VarianteProduit page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(varianteProduitPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create VarianteProduit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/variante-produit/new$'));
        cy.getEntityCreateUpdateHeading('VarianteProduit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', varianteProduitPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/variante-produits',
          body: {
            ...varianteProduitSample,
            taille: taille,
            couleur: couleur,
            produit: produit,
          },
        }).then(({ body }) => {
          varianteProduit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/variante-produits+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/variante-produits?page=0&size=20>; rel="last",<http://localhost/api/variante-produits?page=0&size=20>; rel="first"',
              },
              body: [varianteProduit],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(varianteProduitPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(varianteProduitPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details VarianteProduit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('varianteProduit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', varianteProduitPageUrlPattern);
      });

      it('edit button click should load edit VarianteProduit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('VarianteProduit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', varianteProduitPageUrlPattern);
      });

      it('edit button click should load edit VarianteProduit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('VarianteProduit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', varianteProduitPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of VarianteProduit', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('varianteProduit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', varianteProduitPageUrlPattern);

        varianteProduit = undefined;
      });
    });
  });

  describe('new VarianteProduit page', () => {
    beforeEach(() => {
      cy.visit(varianteProduitPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('VarianteProduit');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of VarianteProduit', () => {
      cy.get(`[data-cy="stock"]`).type('32385');
      cy.get(`[data-cy="stock"]`).should('have.value', '32385');

      cy.get(`[data-cy="taille"]`).select(1);
      cy.get(`[data-cy="couleur"]`).select(1);
      cy.get(`[data-cy="produit"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        varianteProduit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', varianteProduitPageUrlPattern);
    });
  });
});
