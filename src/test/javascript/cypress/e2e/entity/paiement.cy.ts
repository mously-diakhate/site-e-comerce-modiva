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

describe('Paiement e2e test', () => {
  const paiementPageUrl = '/paiement';
  const paiementPageUrlPattern = new RegExp('/paiement(\\?.*)?$');
  let username: string;
  let password: string;
  const paiementSample = { montant: 21858.6, statutPaiement: 'REUSSI' };

  let paiement;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/paiements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/paiements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/paiements/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (paiement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/paiements/${paiement.id}`,
      }).then(() => {
        paiement = undefined;
      });
    }
  });

  it('Paiements menu should load Paiements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('paiement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Paiement').should('exist');
    cy.url().should('match', paiementPageUrlPattern);
  });

  describe('Paiement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(paiementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Paiement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/paiement/new$'));
        cy.getEntityCreateUpdateHeading('Paiement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/paiements',
          body: paiementSample,
        }).then(({ body }) => {
          paiement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/paiements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [paiement],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(paiementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Paiement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('paiement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });

      it('edit button click should load edit Paiement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Paiement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });

      it('edit button click should load edit Paiement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Paiement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);
      });

      it('last delete button click should delete instance of Paiement', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('paiement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paiementPageUrlPattern);

        paiement = undefined;
      });
    });
  });

  describe('new Paiement page', () => {
    beforeEach(() => {
      cy.visit(paiementPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Paiement');
    });

    it('should create an instance of Paiement', () => {
      cy.get(`[data-cy="montant"]`).type('31573.48');
      cy.get(`[data-cy="montant"]`).should('have.value', '31573.48');

      cy.get(`[data-cy="datePaiement"]`).type('2026-08-14T01:42');
      cy.get(`[data-cy="datePaiement"]`).blur();
      cy.get(`[data-cy="datePaiement"]`).should('have.value', '2026-08-14T01:42');

      cy.get(`[data-cy="moyenPaiement"]`).type('du moment que vu que');
      cy.get(`[data-cy="moyenPaiement"]`).should('have.value', 'du moment que vu que');

      cy.get(`[data-cy="referenceTransaction"]`).type('suivant dans la mesure où géométrique');
      cy.get(`[data-cy="referenceTransaction"]`).should('have.value', 'suivant dans la mesure où géométrique');

      cy.get(`[data-cy="statutPaiement"]`).select('REUSSI');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        paiement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', paiementPageUrlPattern);
    });
  });
});
