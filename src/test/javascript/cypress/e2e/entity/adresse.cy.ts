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

describe('Adresse e2e test', () => {
  const adressePageUrl = '/adresse';
  const adressePageUrlPattern = new RegExp('/adresse(\\?.*)?$');
  let username: string;
  let password: string;
  // const adresseSample = {"adresse":"jadis extra","ville":"touriste"};

  let adresse;
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
      url: '/api/cliente-profiles',
      body: {"telephone":"+33 448726144","actif":true},
    }).then(({ body }) => {
      clienteProfile = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/adresses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/adresses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/adresses/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/cliente-profiles', {
      statusCode: 200,
      body: [clienteProfile],
    });

  });
   */

  afterEach(() => {
    if (adresse) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/adresses/${adresse.id}`,
      }).then(() => {
        adresse = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
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

  it('Adresses menu should load Adresses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('adresse');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Adresse').should('exist');
    cy.url().should('match', adressePageUrlPattern);
  });

  describe('Adresse page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(adressePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Adresse page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/adresse/new$'));
        cy.getEntityCreateUpdateHeading('Adresse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', adressePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/adresses',
          body: {
            ...adresseSample,
            clienteProfile: clienteProfile,
          },
        }).then(({ body }) => {
          adresse = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/adresses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/adresses?page=0&size=20>; rel="last",<http://localhost/api/adresses?page=0&size=20>; rel="first"',
              },
              body: [adresse],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(adressePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(adressePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Adresse page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('adresse');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', adressePageUrlPattern);
      });

      it('edit button click should load edit Adresse page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Adresse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', adressePageUrlPattern);
      });

      it('edit button click should load edit Adresse page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Adresse');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', adressePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Adresse', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('adresse').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', adressePageUrlPattern);

        adresse = undefined;
      });
    });
  });

  describe('new Adresse page', () => {
    beforeEach(() => {
      cy.visit(adressePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Adresse');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Adresse', () => {
      cy.get(`[data-cy="libelle"]`).type('sitôt que commis au-dessous de');
      cy.get(`[data-cy="libelle"]`).should('have.value', 'sitôt que commis au-dessous de');

      cy.get(`[data-cy="adresse"]`).type('administration');
      cy.get(`[data-cy="adresse"]`).should('have.value', 'administration');

      cy.get(`[data-cy="ville"]`).type('fourbe vis-à-vie de');
      cy.get(`[data-cy="ville"]`).should('have.value', 'fourbe vis-à-vie de');

      cy.get(`[data-cy="quartier"]`).type('délégation dynamique');
      cy.get(`[data-cy="quartier"]`).should('have.value', 'délégation dynamique');

      cy.get(`[data-cy="telephone"]`).type('0182693499');
      cy.get(`[data-cy="telephone"]`).should('have.value', '0182693499');

      cy.get(`[data-cy="parDefaut"]`).should('not.be.checked');
      cy.get(`[data-cy="parDefaut"]`).click();
      cy.get(`[data-cy="parDefaut"]`).should('be.checked');

      cy.get(`[data-cy="clienteProfile"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        adresse = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', adressePageUrlPattern);
    });
  });
});
