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

describe('ClienteProfile e2e test', () => {
  const clienteProfilePageUrl = '/cliente-profile';
  const clienteProfilePageUrlPattern = new RegExp('/cliente-profile(\\?.*)?$');
  let username: string;
  let password: string;
  // const clienteProfileSample = {};

  let clienteProfile;
  // let user;

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
      url: '/api/users',
      body: {"login":"Adalsinde24","firstName":"Aube","lastName":"Arnaud","email":"Caribert_Nguyen@gmail.com","langKey":"loufoque l","imageUrl":"de sorte que lâche approfondir"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/cliente-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cliente-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cliente-profiles/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/paniers', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/adresses', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/commandes', {
      statusCode: 200,
      body: [],
    });

  });
   */

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

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('ClienteProfiles menu should load ClienteProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cliente-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ClienteProfile').should('exist');
    cy.url().should('match', clienteProfilePageUrlPattern);
  });

  describe('ClienteProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(clienteProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ClienteProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cliente-profile/new$'));
        cy.getEntityCreateUpdateHeading('ClienteProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clienteProfilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cliente-profiles',
          body: {
            ...clienteProfileSample,
            user: user,
          },
        }).then(({ body }) => {
          clienteProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cliente-profiles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cliente-profiles?page=0&size=20>; rel="last",<http://localhost/api/cliente-profiles?page=0&size=20>; rel="first"',
              },
              body: [clienteProfile],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(clienteProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(clienteProfilePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ClienteProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('clienteProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clienteProfilePageUrlPattern);
      });

      it('edit button click should load edit ClienteProfile page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ClienteProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clienteProfilePageUrlPattern);
      });

      it('edit button click should load edit ClienteProfile page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ClienteProfile');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clienteProfilePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ClienteProfile', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('clienteProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clienteProfilePageUrlPattern);

        clienteProfile = undefined;
      });
    });
  });

  describe('new ClienteProfile page', () => {
    beforeEach(() => {
      cy.visit(clienteProfilePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ClienteProfile');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ClienteProfile', () => {
      cy.get(`[data-cy="telephone"]`).type('+33 223585856');
      cy.get(`[data-cy="telephone"]`).should('have.value', '+33 223585856');

      cy.get(`[data-cy="actif"]`).should('not.be.checked');
      cy.get(`[data-cy="actif"]`).click();
      cy.get(`[data-cy="actif"]`).should('be.checked');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        clienteProfile = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', clienteProfilePageUrlPattern);
    });
  });
});
