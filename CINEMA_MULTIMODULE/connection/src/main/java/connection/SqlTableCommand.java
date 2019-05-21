package connection;



import exceptions.ExceptionCode;
import exceptions.MyException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marcin Lupa
 */
public class SqlTableCommand {
    private List<String> commands = new ArrayList<>();


    private SqlTableCommand(SqlTableCommandBuilder builder) {
        this.commands = builder.commands;
    }

    @Override
    public String toString() {

        if (commands.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(commands.get(0));
        sb.append(commands.stream().skip(1).collect(Collectors.joining(", ")));
        sb.append(" );");
        return sb.toString();
    }

    public static SqlTableCommandBuilder builder() {
        return new SqlTableCommandBuilder();
    }

    public static class SqlTableCommandBuilder {
        private List<String> commands = new ArrayList<>();
        /**
         *
         * @param name  name of table
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder table(String name) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (commands.isEmpty()) {
                    commands.add(MessageFormat.format("create table if not exists {0} (", name));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.VALIDATION, "BUILDER SQL_COMMAND_TABLE_BUILDER TABLE EXCEPTION: " + e.getMessage());
            }
        }
        /**
         *
         * @param name  primary key option
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder primaryKey(String name) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (commands.size() == 1) {
                    commands.add(MessageFormat.format("{0} INTEGER PRIMARY KEY autoincrement ", name));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER PRIMARY KEY EXCEPTION: " + e.getMessage());
            }
        }
        /**
         * @param name  name of string column
         * @param options
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder stringColumn(String name, int maxLength, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (maxLength <= 0) {
                    throw new IllegalArgumentException("MAXLENGTH IS NOT CORRECT");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("{0} varchar({1}) {2} ", name, maxLength, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER STRING COLUMN EXCEPTION: " + e.getMessage());
            }
        }

        /**

         * @param name  name of integer column
         * @param options
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder intColumn(String name, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("{0} integer {1} ", name, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER INT COLUMN EXCEPTION: " + e.getMessage());
            }
        }
        /**

         * @param name  name of decimal column
         * @param options
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder decimalColumn(String name, int scale, int precision, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (scale <= 0 || precision <= 0 || scale <= precision) {
                    throw new IllegalArgumentException("SCALE OR PRECISION ARE NOT CORRECT");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("{0} decimal({1}, {2}) {3} ", name, scale, precision, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER DECIMAL COLUMN EXCEPTION: " + e.getMessage());
            }
        }
        /**
         *
         * @param name  name of date column
         * @param options
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder dateColumn(String name, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("{0} DATE {1}", name, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER DATE COLUMN EXCEPTION: " + e.getMessage());
            }
        }

        /**

         * @param name  name of date time column
         * @param options
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder dateTimeColumn(String name, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("{0} DATETIME {1}", name, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER DATE COLUMN EXCEPTION: " + e.getMessage());
            }
        }
        /**

         * @param name column
         * @param options
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder column(String name, String type, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (type == null) {
                    throw new NullPointerException("TYPE IS NULL");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("{0} {1} {2} ", name, type, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER COLUMN EXCEPTION: " + e.getMessage());
            }
        }
        /**

         * @param name   foregin key column name
         * @param options
         * @param foreignColumn column from table foreign
         * @param foreignTable table from table foreign
         * @return SqlTableCommandBuilder instance
         */
        public SqlTableCommandBuilder foreignKey(String name, String foreignTable, String foreignColumn, String options) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (foreignTable == null) {
                    throw new NullPointerException("FOREIGN TABLE IS NULL");
                }

                if (foreignColumn == null) {
                    throw new NullPointerException("FOREIGN COLUMN IS NULL");
                }

                if (options == null) {
                    throw new NullPointerException("OPTIONS STRING IS NULL");
                }

                if (commands.size() >= 2) {
                    commands.add(MessageFormat.format("foreign key ({0}) references {1}({2}) {3} ", name, foreignTable, foreignColumn, options));
                }
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SQL_TABLE_COMMAND_BUILDER FOREIGN KEY EXCEPTION: " + e.getMessage());
            }
        }

        public SqlTableCommand build() {
            return new SqlTableCommand(this);
        }
    }
}
