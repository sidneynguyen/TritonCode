import java.util.StringTokenizer;

public class OperationParser {
    public static Operation strToOperation(String opStr) {
        StringTokenizer tokenizer = new StringTokenizer(opStr, ",");
        Operation operation = new Operation();
        while (tokenizer.hasMoreElements()) {
            String unparsedOp = (String) tokenizer.nextElement();
            if (unparsedOp.charAt(0) == 'R') {
                int length = Integer.parseInt(unparsedOp.substring(1));
                operation.add(new OperationComponent(OperationComponent.OP_COMP_RETAIN, null, length));
            } else if (unparsedOp.charAt(0) == 'I') {
                String value = unparsedOp.substring(1);
                operation.add(new OperationComponent(OperationComponent.OP_COMP_INSERT, value, value.length()));
            } else if (unparsedOp.charAt(0) == 'D') {
                String value = unparsedOp.substring(1);
                operation.add(new OperationComponent(OperationComponent.OP_COMP_DELETE, value, value.length()));
            }

        }
        return operation;
    }
}
