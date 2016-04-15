package travel.snapshot.qa.manager.jboss.configuration;

public final class Authentication {

    private final String username;

    private final String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private Authentication(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
    }

    public static class Builder {

        private String username = "admin";

        private String password = "admin";

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Authentication build() {
            return new Authentication(this);
        }
    }
}
