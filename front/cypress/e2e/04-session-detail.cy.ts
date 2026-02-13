function login(admin: boolean) {
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
        users: [],
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

describe('Session detail page', () => {
  it('should allow admin to delete a session', () => {
    login(true);

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
    }).as('detail');

    cy.intercept('GET', '/api/teacher/2', {
      statusCode: 200,
      body: { id: 2, firstName: 'Emma', lastName: 'Stone', createdAt: '2026-01-01', updatedAt: '2026-01-02' },
    }).as('teacher');

    cy.intercept('DELETE', '/api/session/10', { statusCode: 200, body: {} }).as('deleteSession');

    cy.contains('button', 'Detail').click();
    cy.wait('@detail');
    cy.wait('@teacher');

    cy.contains('button', 'Delete').click();
    cy.wait('@deleteSession');
    cy.url().should('include', '/sessions');
  });

  it('should allow a user to participate and unParticipate', () => {
    login(false);

    let detailCall = 0;
    cy.intercept('GET', '/api/session/10', (req) => {
      detailCall += 1;

      if (detailCall === 1) {
        req.reply({
          statusCode: 200,
          body: {
            id: 10,
            name: 'Morning Flow',
            description: 'Breathing and stretching',
            date: '2026-02-10T00:00:00.000Z',
            teacher_id: 2,
            users: [],
          },
        });
        return;
      }

      if (detailCall === 2) {
        req.reply({
          statusCode: 200,
          body: {
            id: 10,
            name: 'Morning Flow',
            description: 'Breathing and stretching',
            date: '2026-02-10T00:00:00.000Z',
            teacher_id: 2,
            users: [1],
          },
        });
        return;
      }

      req.reply({
        statusCode: 200,
        body: {
          id: 10,
          name: 'Morning Flow',
          description: 'Breathing and stretching',
          date: '2026-02-10T00:00:00.000Z',
          teacher_id: 2,
          users: [],
        },
      });
    }).as('detail');

    cy.intercept('GET', '/api/teacher/2', {
      statusCode: 200,
      body: { id: 2, firstName: 'Emma', lastName: 'Stone', createdAt: '2026-01-01', updatedAt: '2026-01-02' },
    }).as('teacher');

    cy.intercept('POST', '/api/session/10/participate/1', { statusCode: 200, body: {} }).as('participate');
    cy.intercept('DELETE', '/api/session/10/participate/1', { statusCode: 200, body: {} }).as('unParticipate');

    cy.contains('button', 'Detail').click();
    cy.wait('@detail');
    cy.wait('@teacher');

    cy.contains('button', 'Participate').click();
    cy.wait('@participate');
    cy.wait('@detail');
    cy.contains('button', 'Do not participate').should('be.visible');

    cy.contains('button', 'Do not participate').click();
    cy.wait('@unParticipate');
    cy.wait('@detail');
    cy.contains('button', 'Participate').should('be.visible');
  });
});
