.. envvar:: DEMO

A non-empty value causes Nivio to start in demo mode with prepared data. Use the value 'all' to load more landscapes.

.. envvar:: GITHUB_JWT

GitHub JSON Web Token (JWT) to connect to GitHub as a GitHub App.

.. envvar:: GITHUB_LOGIN

GitHub user name. Can also be used to connect as organization with OAuth.

.. envvar:: GITHUB_OAUTH

GitHUb OAuth Token to connect to GitHub via personal access token.

.. envvar:: GITHUB_PASSWORD

GitHub password (for username/password login).

.. envvar:: GITLAB_HOST_URL

The full URL to the GitLab API, e.g. http://your.gitlab.server.com/api/v4.

.. envvar:: GITLAB_PASSWORD

GitLab OAuth login password (optional).

.. envvar:: GITLAB_PERSONAL_ACCESS_TOKEN

Personal token to access the GitLab API at :envvar:`GITLAB_HOST_URL` (optional).

.. envvar:: GITLAB_USERNAME

GitLab OAuth login username (optional). If used, :envvar:`GITLAB_PASSWORD` is also required).

.. envvar:: KUBERNETES_MASTER

K8s master URL (optional). All variables from https://github.com/fabric8io/kubernetes-client#configuring-the-client can be used.

.. envvar:: NIVIO_AUTH_ALLOWED_ORIGINS

Patterns for allowed origins when the app requires authentication

.. envvar:: NIVIO_AUTH_GITHUB_ALIAS_ATTRIBUTE

GitHub user attribute to use as alias

.. envvar:: NIVIO_AUTH_GITHUB_CLIENT_ID

GitHub app OAuth2 client id

.. envvar:: NIVIO_AUTH_GITHUB_CLIENT_SECRET

GitHub app OAuth2 client secret

.. envvar:: NIVIO_AUTH_GITHUB_NAME_ATTRIBUTE

GitHub user attribute to use as name

.. envvar:: NIVIO_AUTH_LOGIN_MODE

Authentication mode: none, optional, required

.. envvar:: NIVIO_BASE_URL

The base URL of Nivio to be used for frontends if running behind a proxy.

.. envvar:: NIVIO_BRANDING_BACKGROUND

Branding background color (hexadecimal only).

.. envvar:: NIVIO_BRANDING_FOREGROUND

Branding foreground color (hexadecimal only).

.. envvar:: NIVIO_BRANDING_LOGO_URL

A URL pointing to a logo.

.. envvar:: NIVIO_BRANDING_MESSAGE

A welcome message on the front page.

.. envvar:: NIVIO_BRANDING_SECONDARY

Accent color used for active elements (hexadecimal only).

.. envvar:: NIVIO_ICON_FOLDER

A folder containing icons named similar to material design icons

.. envvar:: NIVIO_MAIL_HOST

SMTP mail host.

.. envvar:: NIVIO_MAIL_PASSWORD

SMTP mail password.

.. envvar:: NIVIO_MAIL_PORT

SMTP mail port.

.. envvar:: NIVIO_MAIL_USERNAME

SMTP mail username.

.. envvar:: PORT

The port Nivio runs on.

.. envvar:: SEED

A semicolon-separated list of file paths containing landscape configurations.

.. envvar:: SONAR_LOGIN

SonarQube login (username).

.. envvar:: SONAR_PASSWORD

SonarQube password.

.. envvar:: SONAR_PROXY_HOST

SonarQube proxy host (optional).

.. envvar:: SONAR_PROXY_PORT

SonarQube proxy port (optional).

.. envvar:: SONAR_SERVER_URL

SonarQube server URL.