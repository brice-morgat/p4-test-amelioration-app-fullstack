function loginWithRole(admin: boolean) {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      admin,
    },
  }).as('login');

  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: [
      {
        id: 10,
        name: 'Morning Flow',
        description: 'Breathing and stretching',
        date: '2026-02-10T00:00:00.000Z',
        teacher_id: 2,
        users: [1],
      },
    ],
  }).as('sessions');

  cy.visit('/login');
  cy.get('input[formControlName=email]').type('john.doe@mail.com');
  cy.get('input[formControlName=password]').type('secret123');
  cy.contains('button', 'Submit').click();

  cy.wait('@login');
  cy.wait('@sessions');
}

describe('Sessions list page', () => {
  it('should display sessions list', () => {
    loginWithRole(true);

    cy.contains('Rentals available').should('be.visible');
    cy.contains('Morning Flow').should('be.visible');
    cy.contains('Breathing and stretching').should('be.visible');
    cy.contains('button', 'Detail').should('be.visible');
  });

  it('should show create and edit actions for admin', () => {
    loginWithRole(true);

    cy.contains('button', 'Create').should('be.visible');
    cy.contains('button', 'Edit').should('be.visible');
  });

  it('should hide create and edit actions for non-admin', () => {
    loginWithRole(false);

    cy.contains('button', 'Create').should('not.exist');
    cy.contains('button', 'Edit').should('not.exist');
    cy.contains('button', 'Detail').should('be.visible');
  });
});
