function loginAsAdminWithSessions() {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe@mail.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: true,
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

describe('Session form page', () => {
  it('should create a session', () => {
    loginAsAdminWithSessions();

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [
        { id: 2, firstName: 'Emma', lastName: 'Stone', createdAt: '2026-01-01', updatedAt: '2026-01-02' },
      ],
    }).as('teachers');

    cy.intercept('POST', '/api/session', {
      statusCode: 200,
      body: {
        id: 20,
        name: 'Created Session',
        description: 'Session description',
        date: '2026-03-01T00:00:00.000Z',
        teacher_id: 2,
        users: [],
      },
    }).as('createSession');

    cy.contains('button', 'Create').click();
    cy.wait('@teachers');

    cy.get('input[formControlName=name]').type('Created Session');
    cy.get('input[formControlName=date]').type('2026-03-01');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.contains('mat-option', 'Emma Stone').click();
    cy.get('textarea[formControlName=description]').type('Session description');
    cy.contains('button', 'Save').click();

    cy.wait('@createSession');
    cy.url().should('include', '/sessions');
  });

  it('should update a session', () => {
    loginAsAdminWithSessions();

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [
        { id: 2, firstName: 'Emma', lastName: 'Stone', createdAt: '2026-01-01', updatedAt: '2026-01-02' },
      ],
    }).as('teachers');

    cy.intercept('GET', '/api/session/10', {
      statusCode: 200,
      body: {
        id: 10,
        name: 'Morning Flow',
        description: 'Breathing and stretching',
        date: '2026-02-10T00:00:00.000Z',
        teacher_id: 2,
        users: [1],
      },
    }).as('sessionDetail');

    cy.intercept('PUT', '/api/session/10', {
      statusCode: 200,
      body: {
        id: 10,
        name: 'Morning Flow Updated',
        description: 'Updated description',
        date: '2026-02-11T00:00:00.000Z',
        teacher_id: 2,
        users: [1],
      },
    }).as('updateSession');

    cy.contains('button', 'Edit').click();
    cy.wait('@sessionDetail');
    cy.wait('@teachers');

    cy.contains('Update session').should('be.visible');
    cy.get('input[formControlName=name]').clear().type('Morning Flow Updated');
    cy.get('textarea[formControlName=description]').clear().type('Updated description');
    cy.get('input[formControlName=date]').clear().type('2026-02-11');
    cy.contains('button', 'Save').click();

    cy.wait('@updateSession');
    cy.url().should('include', '/sessions');
  });
});
